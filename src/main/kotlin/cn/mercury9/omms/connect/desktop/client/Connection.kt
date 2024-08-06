package cn.mercury9.omms.connect.desktop.client

import icu.takeneko.omms.client.session.ClientInitialSession
import icu.takeneko.omms.client.session.ClientSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.util.concurrent.TimeUnit

sealed interface ConnectionState {
    data object Idle : ConnectionState
    data class Connecting(val id: String) : ConnectionState
    data class Error(val e: Exception) : ConnectionState
    data class Success(val session: ClientSession) : ConnectionState
}

suspend fun connectOmmsServer(
    id: String,
    ip: String,
    port: Int,
    code: Int,
    stateListener: (ConnectionState) -> Unit,
) {
    stateListener(ConnectionState.Connecting(id))
    withContext(Dispatchers.IO) {
        try {
            val clientInitialSession = ClientInitialSession(InetAddress.getByName(ip), port)
            ensureActive()
            val task = future {
                try {
                    val session = clientInitialSession.init(code)
                    stateListener(ConnectionState.Success(session))
                } catch (e: Exception) {
                    stateListener(ConnectionState.Error(e))
                }
            }
            task.orTimeout(1, TimeUnit.MINUTES)
        } catch (e: Exception) {
            stateListener(ConnectionState.Error(e))
        }
    }
}
