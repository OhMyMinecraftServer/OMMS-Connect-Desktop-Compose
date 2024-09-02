package cn.mercury9.omms.connect.desktop.client.omms

import icu.takeneko.omms.client.data.controller.Controller
import icu.takeneko.omms.client.data.controller.Status
import icu.takeneko.omms.client.session.ClientSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

sealed interface FetchControllersState {
    data object Fetching : FetchControllersState
    data class Error(val e: Throwable) : FetchControllersState
    data class Success(val controllers: Map<String, Controller>) : FetchControllersState
}

suspend fun fetchControllersFormServer(
    session: ClientSession,
    stateListener: (FetchControllersState) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            ensureActive()
            future {
                session.fetchControllersFromServer {
                    try {
                        stateListener(FetchControllersState.Success(it))
                    } catch (e: Throwable) {
                        stateListener(FetchControllersState.Error(e))
                    }
                }
            }.orTimeout(3, TimeUnit.MINUTES)
        } catch (e: Throwable) {
            stateListener(FetchControllersState.Error(e))
        }
    }
}

sealed interface FetchControllerStatusState {
    data object Fetching : FetchControllerStatusState
    data class Error(val e: Throwable) : FetchControllerStatusState
    data class Success(val status: Status) : FetchControllerStatusState
}

suspend fun fetchControllerStatusFromServer(
    session: ClientSession,
    controllerId: String,
    stateListener: (FetchControllerStatusState) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            ensureActive()
            future {
                try {
                    session.fetchControllerStatus(
                        controllerId,
                        {stateListener(FetchControllerStatusState.Success(it))},
                        {stateListener(FetchControllerStatusState.Error(Exception(it)))},
                    )
                } catch (e: Throwable) {
                    stateListener(FetchControllerStatusState.Error(e))
                }
            }.orTimeout(3, TimeUnit.MINUTES)
        } catch (e: Throwable) {
            stateListener(FetchControllerStatusState.Error(e))
        }
    }
}
