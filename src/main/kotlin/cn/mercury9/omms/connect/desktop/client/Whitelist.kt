package cn.mercury9.omms.connect.desktop.client

import icu.takeneko.omms.client.session.ClientSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

sealed interface FetchWhitelistState {
    data object Fetching : FetchWhitelistState
    data class Error(val e: Throwable) : FetchWhitelistState
    data class Success(val whitelist: Map<String, List<String>>) : FetchWhitelistState
}

suspend fun fetchWhitelistFromServer(
    session: ClientSession?,
    stateListener: (FetchWhitelistState) -> Unit
) {
    withContext(Dispatchers.IO) {
        if (session == null) {
            stateListener(FetchWhitelistState.Error(RuntimeException("session is null")))
            return@withContext
        }
        try {
            future {
                try {
                    session.fetchWhitelistFromServer {
                        stateListener(FetchWhitelistState.Success(it))
                    }
                } catch (e: Throwable) {
                    stateListener(FetchWhitelistState.Error(RuntimeException(e)))
                }
            }.orTimeout(3, TimeUnit.MINUTES)
        } catch (e: Throwable) {
            stateListener(FetchWhitelistState.Error(e))
        }
    }
}
