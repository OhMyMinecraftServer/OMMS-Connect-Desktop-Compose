package cn.mercury9.omms.connect.desktop.client

import icu.takeneko.omms.client.data.announcement.Announcement
import icu.takeneko.omms.client.session.ClientSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

sealed interface FetchAnnouncementState {
    data object Fetching : FetchAnnouncementState
    data class Error(val throwable: Throwable) : FetchAnnouncementState
    data class Success(val data: Map<String, Announcement>) : FetchAnnouncementState
}

suspend fun fetchAnnouncementFromServer(
    session: ClientSession,
    stateListener: (FetchAnnouncementState) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            ensureActive()
            future {
                session.fetchAnnouncementFromServer {
                    try {
                        stateListener(FetchAnnouncementState.Success(it))
                    } catch (e: Throwable) {
                        stateListener(FetchAnnouncementState.Error(e))
                    }
                }
            }.orTimeout(1, TimeUnit.MINUTES)
        } catch (t: Throwable) {
            stateListener(FetchAnnouncementState.Error(t))
        }
    }
}
