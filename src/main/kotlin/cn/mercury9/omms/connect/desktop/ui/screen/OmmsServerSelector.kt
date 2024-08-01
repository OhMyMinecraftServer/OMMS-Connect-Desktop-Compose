package cn.mercury9.omms.connect.desktop.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.mercury9.compose.utils.painter
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.data.IpLegalState
import cn.mercury9.omms.connect.desktop.data.NameLegalState
import cn.mercury9.omms.connect.desktop.data.PortLegalState
import cn.mercury9.omms.connect.desktop.data.checkIp
import cn.mercury9.omms.connect.desktop.data.checkName
import cn.mercury9.omms.connect.desktop.data.checkPort
import cn.mercury9.omms.connect.desktop.data.configs.OmmsServer
import cn.mercury9.omms.connect.desktop.data.configs.servers
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.add_24px
import cn.mercury9.omms.connect.desktop.resources.add_omms_server
import cn.mercury9.omms.connect.desktop.resources.cancel_24px
import cn.mercury9.omms.connect.desktop.resources.code
import cn.mercury9.omms.connect.desktop.resources.dns_24px
import cn.mercury9.omms.connect.desktop.resources.error_blank
import cn.mercury9.omms.connect.desktop.resources.error_port_oob
import cn.mercury9.omms.connect.desktop.resources.ip
import cn.mercury9.omms.connect.desktop.resources.omms_server_list
import cn.mercury9.omms.connect.desktop.resources.port
import cn.mercury9.omms.connect.desktop.resources.remember_code
import cn.mercury9.omms.connect.desktop.resources.server_name

@Composable
fun OmmsServerSelector(
    modifier: Modifier = Modifier
) {
    var isServerSelectorCollapsed by remember { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = !isServerSelectorCollapsed,
            enter = expandIn(expandFrom = Alignment.Center),
            exit = shrinkOut(shrinkTowards = Alignment.Center)
        ) {
            ExpandedOmmsServerSelector(
                onCollapse = { isServerSelectorCollapsed = true }
            )
        }
        AnimatedVisibility(
            visible = isServerSelectorCollapsed,
            enter = expandIn(expandFrom = Alignment.Center),
            exit = shrinkOut(shrinkTowards = Alignment.Center)
        ) {
            CollapsedOmmsServerSelector {
                isServerSelectorCollapsed = false
            }
        }
    }
}

@Composable
fun ExpandedOmmsServerSelector(
    onCollapse: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        ExpandedOmmsServerListTopBar(
            onCollapse = onCollapse
        )
        HorizontalDivider()
        ExpandedOmmsServerList()
    }
}

@Composable
fun CollapsedOmmsServerSelector(
    onExpand: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentWidth()
            .sizeIn(maxWidth = 64.dp)
    ) {
        IconButton(
            onExpand,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Icon(Res.drawable.dns_24px.painter, null)
        }
        HorizontalDivider()
    }
}

@Composable
fun ExpandedOmmsServerListTopBar(
    onCollapse: () -> Unit,
) {
    var flagOpenAddOmmsServerDialog by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp),
            ) {
                IconButton(
                    onCollapse
                ) {
                    Icon(Res.drawable.dns_24px.painter, null)
                }
                Text(Res.string.omms_server_list.string)
            }
            IconButton(
                { flagOpenAddOmmsServerDialog = true },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp),
            ) {
                Icon(
                    Res.drawable.add_24px.painter,
                    Res.string.add_omms_server.string
                )
            }
        }
    }

    AnimatedVisibility(
        visible = flagOpenAddOmmsServerDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        DialogAddOmmsServer(
            onDismissRequest = {
                flagOpenAddOmmsServerDialog = false
            }
        )
    }
}

@Composable
@Preview
fun DialogAddOmmsServer(
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ip by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("50000") }
    var code by remember { mutableStateOf("") }
    var saveCode by remember { mutableStateOf(false) }

    val width = 400.dp

    var nameLegalState by remember { mutableStateOf(NameLegalState.Legal) }
    var ipLegalState by remember { mutableStateOf(IpLegalState.Legal) }
    var portLegalState by remember { mutableStateOf(PortLegalState.Legal) }

    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    Res.string.add_omms_server.string,
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
                                NameLegalState.Legal -> ""
                                NameLegalState.Blank -> Res.string.error_blank.string
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
                        supportingText = { Text(
                            when (ipLegalState) {
                                IpLegalState.Legal -> ""
                                IpLegalState.Blank -> Res.string.error_blank.string
                            }
                        ) },
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
                    value = code,
                    onValueChange = {
                        code = it.filter { symbol ->
                            symbol.isDigit()
                        }
                    },
                    label = { Text(Res.string.code.string) },
                    supportingText = { Text("") },
                    singleLine = true,
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

                var enableButtonAddServer by remember { mutableStateOf(true) }
                Button( {
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
                    ) return@Button

                    if (enableButtonAddServer) {
                        enableButtonAddServer = false
                        onDismissRequest()
                        val ommsServer = OmmsServer(
                            name = name,
                            ip = ip,
                            port = port.toInt(),
                            code = code.toIntOrNull(),
                        )
                        print(ommsServer)
                        servers.get().apply {
                            add(ommsServer)
                        }.also {
                            servers.set(it)
                        }
                    }
                } ) {
                    Text(Res.string.add_omms_server.string)
                }
            }
        }
    }
}

@Composable
fun ExpandedOmmsServerList(
) {
    val serverList = remember {
        mutableStateListOf<OmmsServer>()
    }
    for (server in servers.get()) {
        serverList += server
    }
    servers.onConfigChange += "OmmsServerSelector-ServerList" to {
        serverList.clear()
        for (server in it) {
            serverList += server
        }
    }
    LazyColumn {
        items(
            items = serverList,
        ) { server ->
            HorizontalDivider()
            ExpandedOmmsServerItem(
                server
            )
        }
    }
}

@Composable
fun ExpandedOmmsServerItem(
    server: OmmsServer
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            Text(
                server.name,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                "${server.ip} : ${server.port}", style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }
        IconButton(
            onClick = {
                servers.get().apply {
                    remove(server)
                }.also {
                    servers.set(it)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Icon(Res.drawable.cancel_24px.painter, null)
        }
    }
}
