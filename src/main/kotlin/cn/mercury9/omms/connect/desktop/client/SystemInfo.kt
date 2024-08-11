package cn.mercury9.omms.connect.desktop.client

import icu.takeneko.omms.client.data.system.SystemInfo
import icu.takeneko.omms.client.session.ClientSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

sealed interface FetchSystemInfoState {
    data object Fetching : FetchSystemInfoState
    data class Error(val e: Throwable) : FetchSystemInfoState
    data class Success(val info: SystemInfo) : FetchSystemInfoState
}

suspend fun fetchSystemInfoFromServer(
    session: ClientSession,
    stateListener: (FetchSystemInfoState) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            ensureActive()
            future {
                session.fetchSystemInfoFromServer {
                    try {
                        stateListener(FetchSystemInfoState.Success(it))
                    } catch (e: Throwable) {
                        stateListener(FetchSystemInfoState.Error(e))
                    }
                }
            }.orTimeout(1, TimeUnit.MINUTES)
        } catch (e: Throwable) {
            stateListener(FetchSystemInfoState.Error(e))
        }
    }
}
