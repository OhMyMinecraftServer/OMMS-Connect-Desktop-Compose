package cn.mercury9.omms.connect.desktop.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.mercury9.compose.utils.painter
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.client.ConnectionState
import cn.mercury9.omms.connect.desktop.client.connectOmmsServer
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.data.CodeLegalState
import cn.mercury9.omms.connect.desktop.data.checkCode
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.app_name
import cn.mercury9.omms.connect.desktop.resources.cancel
import cn.mercury9.omms.connect.desktop.resources.code
import cn.mercury9.omms.connect.desktop.resources.confirm
import cn.mercury9.omms.connect.desktop.resources.error_blank
import cn.mercury9.omms.connect.desktop.resources.error_unknown_error
import cn.mercury9.omms.connect.desktop.resources.fail
import cn.mercury9.omms.connect.desktop.resources.hint_choose_omms_server
import cn.mercury9.omms.connect.desktop.resources.ic_launcher
import cn.mercury9.omms.connect.desktop.resources.login
import cn.mercury9.omms.connect.desktop.resources.login_hint
import cn.mercury9.omms.connect.desktop.resources.success
import cn.mercury9.omms.connect.desktop.resources.welcome
import cn.mercury9.omms.connect.desktop.resources.working
import cn.mercury9.omms.connect.desktop.ui.screen.server.OmmsServerNavigateScreen
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun OmmsServerScreen() {
    var serverId by remember { mutableStateOf(AppContainer.currentOmmsServerId) }

    var serverName by remember { mutableStateOf("") }
    var serverIp by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf(0) }
    var serverCode: Int? by remember { mutableStateOf(null) }

    var connectionState: ConnectionState by remember { mutableStateOf(ConnectionState.Idle) }

    AppContainer.onChangeCurrentOmmsServer += "OmmsServerScreen-OmmsServerId" to {
        serverId = AppContainer.currentOmmsServerId
        it?.let {
            serverName = AppContainer.servers.get()[it]!!.name
            serverIp = AppContainer.servers.get()[it]!!.ip
            serverPort = AppContainer.servers.get()[it]!!.port
        }
        serverCode = AppContainer.servers.get()[it]?.code
        connectionState = ConnectionState.Idle
    }

    if (serverId != null) {
        if (serverCode == null) {
            DialogInputOmmsServerCode({ code ->
                serverCode = code.toInt()
            }) {
                AppContainer.currentOmmsServerId = null
                serverCode = null
            }
        } else {
            GlobalScope.launch {
                connectOmmsServer(
                    serverId!!,
                    serverIp,
                    serverPort,
                    serverCode!!,
                ) {
                    connectionState = it
                    if (it is ConnectionState.Success) {
                        AppContainer.sessions += serverId!! to
                                it.session
                    }
                }
            }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        Welcome(connectionState)
        AnimatedVisibility (
            connectionState is ConnectionState.Success,
            enter = slideIn {
                IntOffset(0, -it.height)
            },
            exit = slideOut {
                IntOffset(-it.width, 0)
            }
        ) {
            OmmsServerNavigateScreen()
        }
    }
}

@Composable
fun DialogInputOmmsServerCode(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var code by remember { mutableStateOf("") }
    var codeLegalState by remember { mutableStateOf(CodeLegalState.Legal) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    ) {
        ElevatedCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .width(IntrinsicSize.Max)
            ) {
                Text(
                    Res.string.login_hint.string,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it.filter { char ->
                            char.isDigit()
                        }
                        codeLegalState = checkCode(it)
                    },
                    label = {
                        Text(Res.string.code.string)
                    },
                    isError = codeLegalState != CodeLegalState.Legal,
                    supportingText = {
                        Text(when (codeLegalState) {
                            CodeLegalState.Legal -> ""
                            CodeLegalState.Blank -> Res.string.error_blank.string
                        })
                    },
                    singleLine = true
                )
                Row {
                    Button(
                        onClick = {onDismiss()},
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    ) {
                        Text(Res.string.cancel.string)
                    }
                    Button(
                        onClick = {
                            if (run {
                                codeLegalState = checkCode(code)
                                codeLegalState == CodeLegalState.Legal
                            }) {
                                onConfirm(code)
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    ) {
                        Text(Res.string.login.string)
                    }
                }
            }
        }
    }
}

@Composable
fun Welcome(
    state: ConnectionState
) {
    var flagOpenErrorDialog by remember { mutableStateOf(true) }
    if (flagOpenErrorDialog && state is ConnectionState.Error) {
        DialogShowError(
            state.e
        ) {
            flagOpenErrorDialog = false
            AppContainer.currentOmmsServerId = null
        }
    }
    AppContainer.onChangeCurrentOmmsServer += "OmmsServerScreen-DialogInputOmmsServerCode-flagOpenErrorDialog" to {
        flagOpenErrorDialog = true
    }

    Box(
        Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Center)
                .shadow(
                    elevation = 64.dp,
                    shape = CircleShape,
                    ambientColor =  MaterialTheme.colorScheme.primary,
                    spotColor = MaterialTheme.colorScheme.primary
                )
                .padding(16.dp)
        ) {
            Image(Res.drawable.ic_launcher.painter, "logo")
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    Res.string.welcome.string,
                    color = MaterialTheme.colorScheme.outline,
                    fontStyle = FontStyle.Italic,
                )
                Text(
                    Res.string.app_name.string,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Box(
                    modifier = Modifier
                        .size(300.dp, 14.dp)
                ) {
                    Text(
                        text = when (state) {
                            ConnectionState.Idle ->
                                Res.string.hint_choose_omms_server.string

                            is ConnectionState.Connecting ->
                                Res.string.working.string(AppContainer.servers.get()[state.id]?.name.toString())

                            is ConnectionState.Error ->
                                Res.string.error_unknown_error.string

                            is ConnectionState.Success ->
                                Res.string.success.string
                        },
                        color = when (state) {
                            is ConnectionState.Error ->
                                MaterialTheme.colorScheme.error
                            else ->
                                MaterialTheme.colorScheme.outline
                        },
                        style = MaterialTheme.typography.labelSmall,
                        overflow = TextOverflow.Visible,
                    )
                }
            }
        }
    }
}

@Composable
fun DialogShowError(
    e: Throwable,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                Res.string.fail.string,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
            ) {
                Text(Res.string.confirm.string)
            }
        },
        text = {
            SelectionContainer {
                Text("$e\n${e.cause}")
            }
        }
    )
}
