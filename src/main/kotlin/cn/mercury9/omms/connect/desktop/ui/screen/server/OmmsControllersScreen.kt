package cn.mercury9.omms.connect.desktop.ui.screen.server

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.mercury9.compose.utils.painter
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.client.FetchControllersState
import cn.mercury9.omms.connect.desktop.client.FetchSystemInfoState
import cn.mercury9.omms.connect.desktop.client.fetchControllersFormServer
import cn.mercury9.omms.connect.desktop.client.fetchSystemInfoFromServer
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.ic_server_default
import cn.mercury9.omms.connect.desktop.resources.ic_server_fabric
import cn.mercury9.omms.connect.desktop.resources.ic_server_linux
import cn.mercury9.omms.connect.desktop.resources.ic_server_windows
import cn.mercury9.omms.connect.desktop.resources.label_controller_type
import cn.mercury9.omms.connect.desktop.resources.label_loading
import cn.mercury9.omms.connect.desktop.resources.server
import icu.takeneko.omms.client.data.controller.Controller
import icu.takeneko.omms.client.data.system.SystemInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun OmmsControllersScreen() {
    var lastFetchedController by remember { mutableStateOf(AppContainer.currentOmmsServerId) }
    var lastFetchedServerInfo by remember { mutableStateOf(AppContainer.currentOmmsServerId) }
    var fetchControllersState: FetchControllersState by remember { mutableStateOf(FetchControllersState.Fetching) }
    var fetchSystemInfoState: FetchSystemInfoState by remember { mutableStateOf(FetchSystemInfoState.Fetching) }

    if (
        fetchControllersState !is FetchControllersState.Success
        || lastFetchedController != AppContainer.currentOmmsServerId
    ) {
        lastFetchedController = AppContainer.currentOmmsServerId
        GlobalScope.launch {
            fetchControllersFormServer(
                AppContainer.sessions[AppContainer.currentOmmsServerId!!]!!
            ) {
                fetchControllersState = it
            }
        }
    }
    if (
        fetchSystemInfoState !is FetchSystemInfoState.Success
        || lastFetchedServerInfo != AppContainer.currentOmmsServerId
    ) {
        lastFetchedServerInfo = AppContainer.currentOmmsServerId
        GlobalScope.launch {
            fetchSystemInfoFromServer(
                AppContainer.sessions[AppContainer.currentOmmsServerId!!]!!
            ) {
                fetchSystemInfoState = it
            }
        }
    }

    AnimatedVisibility(
        fetchControllersState is FetchControllersState.Success
                || fetchSystemInfoState is FetchSystemInfoState.Success,
        enter = slideIn {
            IntOffset(0, -it.height)
        }
    ) {
       OmmsServerControllerList(
           fetchSystemInfoState,
           fetchControllersState
       )
    }
}

@Composable
fun OmmsServerControllerList(
    fetchSystemInfoState: FetchSystemInfoState,
    fetchControllersState: FetchControllersState
) {
    val controllers = remember { mutableStateMapOf<String, Controller>() }
    if (fetchControllersState is FetchControllersState.Success) {
        controllers.clear()
        controllers.putAll(fetchControllersState.controllers)
    } else {
        controllers.clear()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(250.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            item(
                span = StaggeredGridItemSpan.FullLine
            ) {
                Spacer(Modifier.height(8.dp))
            }
            if (fetchSystemInfoState is FetchSystemInfoState.Success) {
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    OmmsServerSystemInfoItem(fetchSystemInfoState.info)
                }
            } else {
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    Text(Res.string.label_loading.string)
                }
            }
            items(
                controllers.values.toList(),
                key = { it.id }
            ) {
                OmmsServerControllerItem(it)
            }
            if (fetchControllersState !is FetchControllersState.Success) {
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    Text(Res.string.label_loading.string)
                }
            }
            item(
                span = StaggeredGridItemSpan.FullLine
            ) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun OmmsServerSystemInfoItem(
    systemInfo: SystemInfo,
) {
    ElevatedCard {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                when (systemInfo.osName) {
                    "Linux" -> Res.drawable.ic_server_linux.painter
                    "Windows" -> Res.drawable.ic_server_windows.painter
                    else -> Res.drawable.ic_server_default.painter
                },
                null
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                Text(
                    Res.string.server.string,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    "${systemInfo.osName} ${systemInfo.osVersion} ${systemInfo.osArch}",
                )
            }
        }
    }
}

@Composable
fun OmmsServerControllerItem(
    controller: Controller,
) {
    ElevatedCard {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row {
                Image(
                    when (controller.type) {
                        "fabric" ->
                            Res.drawable.ic_server_fabric.painter
                        else ->
                        Res.drawable.ic_server_default.painter
                    },
                    null,
                    modifier = Modifier
                        .size(50.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                ) {
                    Text(
                        controller.displayName,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        controller.id,
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.labelSmall,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }
            Text(Res.string.label_controller_type.string(controller.type))
        }
    }
}
