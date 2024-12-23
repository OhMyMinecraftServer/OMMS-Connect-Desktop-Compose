package cn.mercury9.omms.connect.desktop.client.omms

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
    data class Error(val e: Throwable) : ConnectionState
    data class Success(val session: ClientSession) : ConnectionState
}

suspend fun connectOmmsServer(
    id: String,
    ip: String,
    port: Int,
    codeHashed: String,
    stateListener: (ConnectionState) -> Unit,
) {
    withContext(Dispatchers.IO) {
        stateListener(ConnectionState.Connecting(id))
        try {
            val clientInitialSession = ClientInitialSession(InetAddress.getByName(ip), port)
            ensureActive()
            future {
                try {
                    val token = ClientInitialSession.generateTokenFromHashed(codeHashed)
                    val session = clientInitialSession.init(token)
                    stateListener(ConnectionState.Success(session))
                } catch (e: Throwable) {
                    stateListener(ConnectionState.Error(e))
                }
            }.orTimeout(1, TimeUnit.MINUTES)
        } catch (e: Exception) {
            stateListener(ConnectionState.Error(e))
        }
    }
}

fun endOmmsServerConnection(
    session: ClientSession,
    callback: (String) -> Unit
) {
    try {
        session.close()
    } catch (e: Exception) {
        callback(e.localizedMessage)
    }
}
