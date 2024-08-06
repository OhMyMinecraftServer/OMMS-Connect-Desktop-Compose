package cn.mercury9.omms.connect.desktop.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import cn.mercury9.compose.utils.painter
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.data.IpLegalState
import cn.mercury9.omms.connect.desktop.data.NameLegalState
import cn.mercury9.omms.connect.desktop.data.PortLegalState
import cn.mercury9.omms.connect.desktop.data.checkIp
import cn.mercury9.omms.connect.desktop.data.checkName
import cn.mercury9.omms.connect.desktop.data.checkPort
import cn.mercury9.omms.connect.desktop.data.configs.OmmsServer
import cn.mercury9.omms.connect.desktop.data.configs.OmmsServerListSortBy
import cn.mercury9.omms.connect.desktop.data.configs.config
import cn.mercury9.omms.connect.desktop.data.configs.servers
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.add_24px
import cn.mercury9.omms.connect.desktop.resources.add_omms_server
import cn.mercury9.omms.connect.desktop.resources.cancel
import cn.mercury9.omms.connect.desktop.resources.code
import cn.mercury9.omms.connect.desktop.resources.delete
import cn.mercury9.omms.connect.desktop.resources.dns_24px
import cn.mercury9.omms.connect.desktop.resources.edit
import cn.mercury9.omms.connect.desktop.resources.error_blank
import cn.mercury9.omms.connect.desktop.resources.error_name_too_long
import cn.mercury9.omms.connect.desktop.resources.error_port_oob
import cn.mercury9.omms.connect.desktop.resources.hint_add_omms_server
import cn.mercury9.omms.connect.desktop.resources.hint_confirm_delete
import cn.mercury9.omms.connect.desktop.resources.ip
import cn.mercury9.omms.connect.desktop.resources.label_no_more
import cn.mercury9.omms.connect.desktop.resources.login
import cn.mercury9.omms.connect.desktop.resources.more_vert_24px
import cn.mercury9.omms.connect.desktop.resources.omms_server_list
import cn.mercury9.omms.connect.desktop.resources.port
import cn.mercury9.omms.connect.desktop.resources.remember_code
import cn.mercury9.omms.connect.desktop.resources.save
import cn.mercury9.omms.connect.desktop.resources.send_24px
import cn.mercury9.omms.connect.desktop.resources.server_name
import cn.mercury9.omms.connect.desktop.resources.title_delete_omms_server
import cn.mercury9.omms.connect.desktop.resources.title_edit_omms_server
import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun OmmsServerSelector(
    modifier: Modifier = Modifier
) {
    var isServerSelectorCollapsed by remember { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) {
        OmmsServerSelector(
            isServerSelectorCollapsed
        ) {
            isServerSelectorCollapsed = !isServerSelectorCollapsed
        }
    }
}

@Composable
fun OmmsServerSelector(
    isCollapsed: Boolean,
    onClickCEButton: () -> Unit
) {
    val width = animateDpAsState(
        if (isCollapsed) 64.dp else 250.dp
    )
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(width.value)
    ) {
        OmmsServerListTopBar(
            isCollapsed,
            onClickCEButton
        )
        HorizontalDivider()
        AnimatedVisibility(
            !isCollapsed,
            enter = expandIn(
                expandFrom = Alignment.TopStart
            ),
            exit = shrinkOut(
                shrinkTowards = Alignment.TopStart
            )
        ) {
            ExpandedOmmsServerList()
        }
    }
}

@Composable
fun OmmsServerListTopBar(
    isCollapsed: Boolean,
    onClickCEButton: () -> Unit,
) {
    var flagOpenAddOmmsServerDialog by remember { mutableStateOf(false) }
    Surface(
        modifier = if (isCollapsed) {
            Modifier
                .size(64.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .height(64.dp)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 8.dp)
                    .height(64.dp),
            ) {
                IconButton(
                    onClickCEButton
                ) {
                    Icon(Res.drawable.dns_24px.painter, null)
                }
                AnimatedVisibility(
                    !isCollapsed,
                    enter = expandIn(
                        expandFrom = Alignment.CenterStart
                    ),
                    exit = shrinkOut(
                        shrinkTowards = Alignment.CenterStart
                    )
                ) {
                    Text(Res.string.omms_server_list.string)
                }
            }
            if (!isCollapsed) {
                IconButton({
                    flagOpenAddOmmsServerDialog = true
                },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    Icon(
                        Res.drawable.add_24px.painter,
                        Res.string.add_omms_server.string
                    )
                }
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
                id = Clock.System.now().toEpochMilliseconds().toString(),
                name = name,
                ip = ip,
                port = port.toInt(),
                code = if (saveCode) code.toIntOrNull() else null,
            )
            servers.get().apply {
                put(
                    ommsServer.id,
                    ommsServer
                )
            }.also {
                servers.set(it)
            }
        }
    }

    val onKeyEventName = "OmmsServerSelector-DialogAddOmmsServer-OnConfirm"
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
                    enabled = saveCode,
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

                Button(::onConfirm) {
                    Text(Res.string.add_omms_server.string)
                }
            }
        }
    }
}

@Composable
fun ExpandedOmmsServerList() {
    val serverList = remember {
        mutableStateMapOf<String, OmmsServer>()
        // 有一个未知问题导致会重复添加，所以我把 `id` 作为 `map` 的 `key`，就会自动去重了哈哈哈
    }
    for (server in servers.get()) {
        // 初始化
        serverList[server.key] = server.value
    }
    servers.onConfigChange += "OmmsServerSelector-ExpandedOmmsServerList-ServerList" to {
        // 更新
        serverList.clear()
        for (server in servers.get()) {
            serverList[server.key] = server.value
        }
    }
    if (serverList.isNotEmpty()) {

        var sortBy by remember { mutableStateOf(config.get().ommsServerListSortBy) }
        config.onConfigChange += "OmmsServerSelector-ExpandedOmmsServerList-SortBy" to {
            sortBy = config.get().ommsServerListSortBy
        }

        var currentOmmsServerId by remember { mutableStateOf(AppContainer.currentOmmsServerId) }
        AppContainer.onChangeCurrentOmmsServer += "OmmsServerList-CurrentOmmsServerId" to {
            currentOmmsServerId = AppContainer.currentOmmsServerId
        }

        LazyColumn {
            items(
                items = serverList.values.toList()
                    .sortedBy(
                        when (sortBy) {
                            OmmsServerListSortBy.Id -> { it ->
                                it.id
                            }

                            OmmsServerListSortBy.Name -> { it ->
                                it.name
                            }
                        }
                    ),
                key = { server -> server.id }
            ) { server ->
                ExpandedOmmsServerItem(
                    server,
                    currentOmmsServerId,
                )
            }
            item {
                Text(
                    Res.string.label_no_more.string,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                Res.string.hint_add_omms_server.string,
                style = MaterialTheme.typography.labelSmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun ExpandedOmmsServerItem(
    server: OmmsServer,
    currentServerId: String?,
) {
    val surfaceColor by animateColorAsState(
        targetValue =  if (server.id == currentServerId) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.surface
            }
    )
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp, 8.dp, 8.dp, 0.dp)
    ) {
        Surface(
            color = surfaceColor,
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp, 16.dp, 0.dp, 16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .widthIn(max = 172.dp)
                ) {
                    Text(
                        server.name,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        "${server.ip} : ${server.port}", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
                var flagPopupMenu by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    IconButton(
                        enabled = server.id != currentServerId,
                        onClick = {
                            AppContainer.currentOmmsServerId = server.id
                        }
                    ) {
                        Icon(Res.drawable.send_24px.painter, Res.string.login.string)
                    }
                    IconButton(
                        enabled = server.id != currentServerId,
                        onClick = {
                            flagPopupMenu = true
                        },
                    ) {
                        Icon(Res.drawable.more_vert_24px.painter, null)
                    }
                    AnimatedVisibility(visible = flagPopupMenu) {
                        Popup(
                            Alignment.TopStart,
                            onDismissRequest = { flagPopupMenu = false }
                        ) {
                            OmmsServerItemMenu(
                                server.id,
                                modifier = Modifier
                                    .sizeIn(maxWidth = 100.dp)
                            ) {
                                flagPopupMenu = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OmmsServerItemMenu(
    serverId: String,
    modifier: Modifier = Modifier
        .wrapContentSize(),
    onDismissRequest: () -> Unit
) {
    var flagOpenEditServerDialog by remember { mutableStateOf(false) }
    var flagOpenDeleteConfirmDialog by remember { mutableStateOf(false) }
    if (flagOpenDeleteConfirmDialog) {
        DialogDeleteOmmsServer(serverId) {
            onDismissRequest()
        }
    } else if (flagOpenEditServerDialog) {
        DialogEditOmmsServer(serverId) {
            onDismissRequest()
        }
    } else {
        ElevatedCard(
            modifier = modifier
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .clickable {
                            if (
                                flagOpenEditServerDialog
                                ||flagOpenDeleteConfirmDialog
                            ) {
                                return@clickable
                            }
                            flagOpenEditServerDialog = true
                        }
                        .size(100.dp, 40.dp)
                ) {
                    Text(
                        Res.string.edit.string,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                HorizontalDivider()
                Box(
                    modifier = Modifier
                        .clickable {
                            if (
                                flagOpenEditServerDialog
                                ||flagOpenDeleteConfirmDialog
                            ) {
                                return@clickable
                            }
                            flagOpenDeleteConfirmDialog = true
                        }
                        .size(100.dp, 40.dp)
                ) {
                    Text(
                        Res.string.delete.string,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun DialogDeleteOmmsServer(
    serverId: String,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard{
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(IntrinsicSize.Max)
            ) {
                Text(
                    Res.string.title_delete_omms_server.string,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Text(
                    Res.string.hint_confirm_delete.string(
                        servers.get()[serverId]!!.name
                    ).replace(
                        "吗",
                        if (Random.nextInt(0..99) == 0) { "🐴" } else "吗"
                    ),
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .height(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                onDismissRequest()
                            }
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        Text(
                            Res.string.cancel.string,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                    VerticalDivider()
                    Box(
                        modifier = Modifier
                            .clickable {
                                onDismissRequest()
                                servers.get().apply {
                                    remove(serverId)
                                }.also {
                                    servers.set(it)
                                }
                            }
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        Text(
                            Res.string.delete.string,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogEditOmmsServer(
    serverId: String,
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf(servers.get()[serverId]!!.name) }
    var ip by remember { mutableStateOf(servers.get()[serverId]!!.ip) }
    var port by remember { mutableStateOf(servers.get()[serverId]!!.port.toString()) }
    var code by remember { mutableStateOf(servers.get()[serverId]!!.code?.toString() ?: "") }
    var saveCode by remember { mutableStateOf(servers.get()[serverId]!!.code != null) }

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
                code = if (saveCode) code.toIntOrNull() else null,
            )
            servers.get().apply {
                replace(serverId, ommsServer)
            }.also {
                servers.set(it)
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
                    enabled = saveCode,
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

                Button(::onConfirm) {
                    Text(Res.string.save.string)
                }
            }
        }
    }
}
