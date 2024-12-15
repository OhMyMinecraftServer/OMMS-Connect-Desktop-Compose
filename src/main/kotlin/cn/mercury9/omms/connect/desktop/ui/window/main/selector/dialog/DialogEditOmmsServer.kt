package cn.mercury9.omms.connect.desktop.ui.window.main.selector.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.data.IpLegalState
import cn.mercury9.omms.connect.desktop.data.NameLegalState
import cn.mercury9.omms.connect.desktop.data.PortLegalState
import cn.mercury9.omms.connect.desktop.data.checkIp
import cn.mercury9.omms.connect.desktop.data.checkName
import cn.mercury9.omms.connect.desktop.data.checkPort
import cn.mercury9.omms.connect.desktop.data.configs.OmmsServer
import cn.mercury9.omms.connect.desktop.data.getHashedCode
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.code
import cn.mercury9.omms.connect.desktop.resources.error_blank
import cn.mercury9.omms.connect.desktop.resources.error_name_too_long
import cn.mercury9.omms.connect.desktop.resources.error_port_oob
import cn.mercury9.omms.connect.desktop.resources.ip
import cn.mercury9.omms.connect.desktop.resources.port
import cn.mercury9.omms.connect.desktop.resources.remember_code
import cn.mercury9.omms.connect.desktop.resources.save
import cn.mercury9.omms.connect.desktop.resources.server_name
import cn.mercury9.omms.connect.desktop.resources.title_edit_omms_server
import cn.mercury9.utils.compose.string

@Composable
fun DialogEditOmmsServer(
    serverId: String,
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf(AppContainer.servers[serverId]!!.name) }
    var ip by remember { mutableStateOf(AppContainer.servers[serverId]!!.ip) }
    var port by remember { mutableStateOf(AppContainer.servers[serverId]!!.port.toString()) }
    var saveCode by remember { mutableStateOf(AppContainer.servers[serverId]!!.codeHashed != null) }
    val codeHashed by remember { mutableStateOf(AppContainer.servers[serverId]!!.codeHashed) }
    var code by remember { mutableStateOf(AppContainer.servers[serverId]!!.codeHashed) }

    val width = 400.dp

    var nameLegalState by remember { mutableStateOf(NameLegalState.Legal) }
    var ipLegalState by remember { mutableStateOf(IpLegalState.Legal) }
    var portLegalState by remember { mutableStateOf(PortLegalState.Legal) }

    var enableButtonAddServer by remember { mutableStateOf(true) }

    fun onConfirm() {
        if (
            run {
                nameLegalState = checkName(name)
                nameLegalState != NameLegalState.Legal
            } || run {
                ipLegalState = checkIp(ip)
                ipLegalState != IpLegalState.Legal
            } || run {
                portLegalState = checkPort(port)
                portLegalState != PortLegalState.Legal
            }
        ) return

        if (enableButtonAddServer) {
            enableButtonAddServer = false
            onDismissRequest()
            val ommsServer = OmmsServer(
                id = serverId,
                name = name,
                ip = ip,
                port = port.toInt(),
                codeHashed = if (saveCode) {
                    if (code == codeHashed)
                        code
                    else
                        getHashedCode(code)
                } else null,
            )
            AppContainer.servers.apply {
                replace(serverId, ommsServer)
            }.also {
                AppContainer.servers = it
            }
        }
    }

    val onKeyEventName = "OmmsServerSelector-DialogEditOmmsServer-OnConfirm"
    AppContainer.onKeyEvent += onKeyEventName to {
        if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
            onConfirm()
            AppContainer.onKeyEvent.remove(onKeyEventName)
            true
        } else false
    }

    Dialog(
        onDismissRequest = {
            AppContainer.onKeyEvent.remove(onKeyEventName)
            onDismissRequest()
        }
    ) {
        ElevatedCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    Res.string.title_edit_omms_server.string,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameLegalState = checkName(name)
                    },
                    label = { Text(Res.string.server_name.string) },
                    isError = when (nameLegalState) {
                        NameLegalState.Legal -> false
                        else -> true
                    },
                    supportingText = {
                        Text(
                            when (nameLegalState) {
                                NameLegalState.Legal ->
                                    ""

                                NameLegalState.Blank ->
                                    Res.string.error_blank.string

                                NameLegalState.TooLong ->
                                    Res.string.error_name_too_long.string
                            }
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .width(width)
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(width)
                ) {
                    OutlinedTextField(
                        value = ip,
                        onValueChange = {
                            ip = it
                            ipLegalState = checkIp(ip)
                        },
                        label = { Text(Res.string.ip.string) },
                        isError = when (ipLegalState) {
                            IpLegalState.Legal -> false
                            else -> true
                        },
                        supportingText = {
                            Text(
                                when (ipLegalState) {
                                    IpLegalState.Legal -> ""
                                    IpLegalState.Blank -> Res.string.error_blank.string
                                }
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(4f)
                    )

                    Spacer(Modifier.width(16.dp))

                    OutlinedTextField(
                        value = port,
                        onValueChange = {
                            port = it.filter { symbol ->
                                symbol.isDigit()
                            }
                            portLegalState = checkPort(port)
                        },
                        label = { Text(Res.string.port.string) },
                        isError = when (portLegalState) {
                            PortLegalState.Legal -> false
                            else -> true
                        },
                        supportingText = {
                            Text(
                                when (portLegalState) {
                                    PortLegalState.Legal -> ""
                                    PortLegalState.Blank -> Res.string.error_blank.string
                                    PortLegalState.OutOfRange -> Res.string.error_port_oob.string
                                }
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(3f)
                    )
                }

                OutlinedTextField(
                    value = code ?: "",
                    enabled = saveCode,
                    onValueChange = {
                        code = it.filter { symbol ->
                            symbol.isDigit()
                        }
                    },
                    label = { Text(Res.string.code.string) },
                    supportingText = { Text("") },
                    singleLine = true,
                    visualTransformation = if (code == codeHashed) {
                        PasswordVisualTransformation()
                    } else VisualTransformation.None,
                    modifier = Modifier
                        .width(width)
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = saveCode,
                        onCheckedChange = {
                            saveCode = it
                        }
                    )
                    Text(Res.string.remember_code.string)
                }

                Button(::onConfirm) {
                    Text(Res.string.save.string)
                }
            }
        }
    }
}