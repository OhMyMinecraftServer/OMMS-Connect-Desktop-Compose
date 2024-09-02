package cn.mercury9.omms.connect.desktop.ui.window.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import kotlinx.datetime.Clock
import kotlin.collections.set
import kotlin.random.Random
import kotlin.random.nextInt
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.data.IpLegalState
import cn.mercury9.omms.connect.desktop.data.NameLegalState
import cn.mercury9.omms.connect.desktop.data.PortLegalState
import cn.mercury9.omms.connect.desktop.data.checkIp
import cn.mercury9.omms.connect.desktop.data.checkName
import cn.mercury9.omms.connect.desktop.data.checkPort
import cn.mercury9.omms.connect.desktop.data.configs.OmmsServer
import cn.mercury9.omms.connect.desktop.data.configs.OmmsServerListSortBy
import cn.mercury9.omms.connect.desktop.data.getHashedCode
import cn.mercury9.omms.connect.desktop.resources.*
import cn.mercury9.utils.compose.painter
import cn.mercury9.utils.compose.string

@Composable
fun OmmsServerSelector(
    modifier: Modifier = Modifier
) {
    var isServerSelectorCollapsed by remember { mutableStateOf(false) }
    AnimatedVisibility(
        !isServerSelectorCollapsed,
        enter = slideIn {
            IntOffset(-it.width, 0)
        } + expandIn(expandFrom = Alignment.CenterStart) {
            IntSize(0, it.height)
        },
        exit = slideOut {
            IntOffset(-it.width, 0)
        } + shrinkOut(shrinkTowards = Alignment.CenterStart) {
            IntSize(0, it.height)
        }
    ) {
        Row {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = modifier
            ) {
                OmmsServerSelectorContainer {
                    isServerSelectorCollapsed = !isServerSelectorCollapsed
                }
            }
            VerticalDivider()
        }
    }
    val interactionSourceRange = remember { MutableInteractionSource() }
    val hoverOnRange by interactionSourceRange.collectIsHoveredAsState()
    val interactionSourceRangeCenter = remember { MutableInteractionSource() }
    val hoverOnRangeCenter by interactionSourceRangeCenter.collectIsHoveredAsState()
    val interactionSourceButton = remember { MutableInteractionSource() }
    val hoverOnButton by interactionSourceButton.collectIsHoveredAsState()
    val buttonOffsetX = animateDpAsState(
        if (hoverOnButton) 8.dp
        else if (hoverOnRangeCenter) (-4).dp
        else if (hoverOnRange) (-32).dp
        else (-64).dp
    )
    val buttonShapeCorner = animateDpAsState(
        if (hoverOnButton) 32.dp
        else 16.dp
    )
    if (isServerSelectorCollapsed) {
        Popup(
            Alignment.CenterStart
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(AppContainer.mainWindowState.size.height - 260.dp)
                    .width(64.dp)
                    .hoverable(interactionSourceRange)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp, 128.dp)
                        .hoverable(interactionSourceRangeCenter)
                ) {
                    FloatingActionButton(
                        onClick =  {
                            isServerSelectorCollapsed = false
                        },
                        shape = RoundedCornerShape(buttonShapeCorner.value),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = buttonOffsetX.value, y = 0.dp)
                            .hoverable(interactionSourceButton)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    }
                }
            }
        }
    }
}

@Composable
fun OmmsServerSelectorContainer(
    onClickCEButton: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
    ) {
        OmmsServerListTopBar(
            onClickCEButton
        )
        HorizontalDivider()
        OmmsServerList()
    }
}

@Composable
fun OmmsServerListTopBar(
    onClickCEButton: () -> Unit,
) {
    var flagOpenAddOmmsServerDialog by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
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
                Text(Res.string.omms_server_list.string)
            }
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
    val codeHashed by remember { mutableStateOf("") }
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
                codeHashed = if (saveCode) {
                    if (code == codeHashed) {
                        code
                    } else getHashedCode(code)
                } else null,
            )
            AppContainer.servers.get().apply {
                put(
                    ommsServer.id,
                    ommsServer
                )
            }.also {
                AppContainer.servers.set(it)
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
fun OmmsServerList() {
    val serverList = remember {
        mutableStateMapOf<String, OmmsServer>()
        // 有一个未知问题导致会重复添加，所以我把 `id` 作为 `map` 的 `key`，就会自动去重了哈哈哈
    }
    for (server in AppContainer.servers.get()) {
        // 初始化
        serverList[server.key] = server.value
    }
    AppContainer.servers.onConfigChange += "OmmsServerSelector-ExpandedOmmsServerList-ServerList" to {
        // 更新
        serverList.clear()
        for (server in AppContainer.servers.get()) {
            serverList[server.key] = server.value
        }
    }
    if (serverList.isNotEmpty()) {

        var sortBy by remember { mutableStateOf(AppContainer.config.get().ommsServerListSortBy) }
        AppContainer.config.onConfigChange += "OmmsServerSelector-ExpandedOmmsServerList-SortBy" to {
            sortBy = AppContainer.config.get().ommsServerListSortBy
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
                OmmsServerItem(
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
fun OmmsServerItem(
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
                        AppContainer.servers.get()[serverId]!!.name
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
                                AppContainer.servers.get().apply {
                                    remove(serverId)
                                }.also {
                                    AppContainer.servers.set(it)
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
    var name by remember { mutableStateOf(AppContainer.servers.get()[serverId]!!.name) }
    var ip by remember { mutableStateOf(AppContainer.servers.get()[serverId]!!.ip) }
    var port by remember { mutableStateOf(AppContainer.servers.get()[serverId]!!.port.toString()) }
    var saveCode by remember { mutableStateOf(AppContainer.servers.get()[serverId]!!.codeHashed != null) }
    val codeHashed by remember { mutableStateOf(AppContainer.servers.get()[serverId]!!.codeHashed) }
    var code by remember { mutableStateOf(AppContainer.servers.get()[serverId]!!.codeHashed) }

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
            AppContainer.servers.get().apply {
                replace(serverId, ommsServer)
            }.also {
                AppContainer.servers.set(it)
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
