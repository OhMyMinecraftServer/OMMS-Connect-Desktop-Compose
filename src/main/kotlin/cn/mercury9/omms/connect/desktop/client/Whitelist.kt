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
    session: ClientSession,
    stateListener: (FetchWhitelistState) -> Unit
) {
    withContext(Dispatchers.IO) {
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

suspend fun addPlayerToWhitelist(
    session: ClientSession,
    whitelist: String,
    player: String,
    stateListener: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            future {
                session.addToWhitelist(
                    whitelist,
                    player,
                    { a, b ->
                        stateListener("$a\n$b")
                    },
                    { a, b ->
                        stateListener("$a\n$b")
                    },
                )
            }.orTimeout(3, TimeUnit.MINUTES)
        } catch (e: Throwable) {
            stateListener(e.toString())
        }
    }
}

suspend fun removePlayerFromWhitelist(
    session: ClientSession,
    whitelist: String,
    player: String,
    stateListener: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            future {
                session.removeFromWhitelist(
                    whitelist,
                    player,
                    { a, b ->
                        stateListener("$a\n$b")
                    },
                    { a, b ->
                        stateListener("$a\n$b")
                    },
                )
            }.orTimeout(3, TimeUnit.MINUTES)
        } catch (e: Throwable) {
            stateListener(e.toString())
        }
    }
}
