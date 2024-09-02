package cn.mercury9.omms.connect.desktop.client.omms

import icu.takeneko.omms.client.data.chatbridge.MessageCache
import icu.takeneko.omms.client.session.ClientSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

sealed interface FetchChatHistoryState {
    data object Fetching : FetchChatHistoryState
    data class Error(val throwable: Throwable) : FetchChatHistoryState
    data class Success(val messageCache: MessageCache) : FetchChatHistoryState
}

suspend fun fetchChatHistoryFromServer(
    session: ClientSession,
    stateListener: (FetchChatHistoryState) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            ensureActive()
            future {
                session.getChatHistory {
                    try {
                        stateListener(FetchChatHistoryState.Success(it))
                    } catch (e: Throwable) {
                        stateListener(FetchChatHistoryState.Error(e))
                    }
                }
            }.orTimeout(1, TimeUnit.MINUTES)
        } catch (e: Throwable) {
            stateListener(FetchChatHistoryState.Error(e))
        }
    }
}

suspend fun sendChatMessage(
    session: ClientSession,
    channel: String,
    message: String,
    callback: (String, String) -> Unit = { _, _ -> }
) {
    withContext(Dispatchers.IO) {
        session.sendChatbridgeMessage(channel, message, callback)
    }
}
