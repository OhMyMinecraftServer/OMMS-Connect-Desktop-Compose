package cn.mercury9.omms.connect.desktop.ui.window.main.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.data.config.OmmsServer
import cn.mercury9.omms.connect.desktop.data.config.OmmsServerListSortBy
import cn.mercury9.omms.connect.desktop.data.config.configState.AppConfigState
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.add_24px
import cn.mercury9.omms.connect.desktop.resources.add_omms_server
import cn.mercury9.omms.connect.desktop.resources.delete
import cn.mercury9.omms.connect.desktop.resources.dns_24px
import cn.mercury9.omms.connect.desktop.resources.edit
import cn.mercury9.omms.connect.desktop.resources.hint_add_omms_server
import cn.mercury9.omms.connect.desktop.resources.label_no_more
import cn.mercury9.omms.connect.desktop.resources.login
import cn.mercury9.omms.connect.desktop.resources.more_vert_24px
import cn.mercury9.omms.connect.desktop.resources.omms_server_list
import cn.mercury9.omms.connect.desktop.resources.send_24px
import cn.mercury9.omms.connect.desktop.ui.window.main.selector.dialog.DialogAddOmmsServer
import cn.mercury9.omms.connect.desktop.ui.window.main.selector.dialog.DialogDeleteOmmsServer
import cn.mercury9.omms.connect.desktop.ui.window.main.selector.dialog.DialogEditOmmsServer
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
fun OmmsServerList() {
    if (AppContainer.servers.isNotEmpty()) {

        val sortBy by remember { AppConfigState.ommsServerListSortBy }

        var currentOmmsServerId by AppContainer.currentOmmsServerId

        LazyColumn {
            items(
                items = AppContainer.servers.values.toList()
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
                MaterialTheme.colorScheme.surfaceVariant
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
                            AppContainer.currentOmmsServerId.value = server.id
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
