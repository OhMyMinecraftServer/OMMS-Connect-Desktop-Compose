package cn.mercury9.omms.connect.desktop.ui.screen.server

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.mercury9.compose.utils.painter
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.client.FetchControllerStatusState
import cn.mercury9.omms.connect.desktop.client.FetchControllersState
import cn.mercury9.omms.connect.desktop.client.FetchSystemInfoState
import cn.mercury9.omms.connect.desktop.client.fetchControllerStatusFromServer
import cn.mercury9.omms.connect.desktop.client.fetchControllersFormServer
import cn.mercury9.omms.connect.desktop.client.fetchSystemInfoFromServer
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.arrow_back_24px
import cn.mercury9.omms.connect.desktop.resources.cancel_24px
import cn.mercury9.omms.connect.desktop.resources.check_circle_fill_24px
import cn.mercury9.omms.connect.desktop.resources.error
import cn.mercury9.omms.connect.desktop.resources.error_fill_24px
import cn.mercury9.omms.connect.desktop.resources.ic_server_default
import cn.mercury9.omms.connect.desktop.resources.ic_server_fabric
import cn.mercury9.omms.connect.desktop.resources.ic_server_linux
import cn.mercury9.omms.connect.desktop.resources.ic_server_windows
import cn.mercury9.omms.connect.desktop.resources.label_controller_type
import cn.mercury9.omms.connect.desktop.resources.label_loading
import cn.mercury9.omms.connect.desktop.resources.label_no_player
import cn.mercury9.omms.connect.desktop.resources.label_state_running
import cn.mercury9.omms.connect.desktop.resources.label_state_stopped
import cn.mercury9.omms.connect.desktop.resources.load_average
import cn.mercury9.omms.connect.desktop.resources.memory
import cn.mercury9.omms.connect.desktop.resources.player_count
import cn.mercury9.omms.connect.desktop.resources.player_list
import cn.mercury9.omms.connect.desktop.resources.refresh_24px
import cn.mercury9.omms.connect.desktop.resources.remain
import cn.mercury9.omms.connect.desktop.resources.server
import cn.mercury9.omms.connect.desktop.resources.status_waiting
import cn.mercury9.omms.connect.desktop.resources.swap
import cn.mercury9.omms.connect.desktop.resources.total
import cn.mercury9.omms.connect.desktop.resources.unavailable
import cn.mercury9.omms.connect.desktop.resources.used
import icu.takeneko.omms.client.data.controller.Controller
import icu.takeneko.omms.client.data.system.FileSystemInfo
import icu.takeneko.omms.client.data.system.SystemInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
    var showSystemInfo by remember { mutableStateOf(false) }
    var showController by remember { mutableStateOf(false) }
    var currentShowController: Controller? by remember { mutableStateOf(null) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = !showSystemInfo && !showController,
            enter = fadeIn(),
            exit = fadeOut()
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
                        OmmsServerSystemInfoItem(fetchSystemInfoState.info) {
                            showSystemInfo = true
                        }
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
                    OmmsServerControllerItem(it) {
                        showController = true
                        currentShowController = it
                    }
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
        AnimatedVisibility(
            visible = showSystemInfo,
            enter = slideIn {
                IntOffset(it.width, 0)
            },
            exit = slideOut {
                IntOffset(it.width, 0)
            }
        ) {
            OmmsServerInfo(
                (fetchSystemInfoState as FetchSystemInfoState.Success).info
            ) {
                showSystemInfo = false
            }
        }
        AnimatedVisibility(
            visible = showController,
            enter = slideIn {
                IntOffset(it.width, 0)
            },
            exit = slideOut {
                IntOffset(it.width, 0)
            }
        ) {
            OmmsServerController(currentShowController!!) {
                showController = false
            }
        }
    }
}

@Composable
fun OmmsServerSystemInfoItem(
    systemInfo: SystemInfo,
    modifier: Modifier = Modifier
        .padding(8.dp),
    onClick: () -> Unit = {},
) {
    ElevatedCard(
        onClick = onClick,
    ) {
        Row(modifier = modifier) {
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
fun OmmsServerInfo(
    systemInfo: SystemInfo,
    onClickButtonBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(550.dp)
                .align(Alignment.Center)
        ) {
            LazyVerticalStaggeredGrid(
                StaggeredGridCells.Adaptive(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    OmmsServerSystemInfoItem(
                        systemInfo,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    ElevatedCard {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                "CPU: " + systemInfo.processorInfo.processorName,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Surface(
                                shape = CircleShape,    // ProgressIndicator 居然不带圆角，也不带 shape ！
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(8.dp)
                            ) {
                                LinearProgressIndicator(
                                    { systemInfo.processorInfo.cpuLoadAvg.toFloat() / systemInfo.processorInfo.logicalProcessorCount },
                                    Modifier
                                        .height(8.dp)
                                )
                            }
                            Text(
                                Res.string.load_average.string + systemInfo.processorInfo.cpuLoadAvg,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            val memoryUsedGb =
                                (systemInfo.memoryInfo.memoryUsed / (1024 * 1024 * 1024.0) * 10).roundToInt() / 10.0
                            val memoryTotalGb =
                                (systemInfo.memoryInfo.memoryTotal / (1024 * 1024 * 1024.0) * 10).roundToInt() / 10.0
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    { (memoryUsedGb / memoryTotalGb).toFloat() },
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeWidth = 8.dp,
                                    modifier = Modifier
                                        .size(64.dp)
                                )
                                Column {
                                    Text(
                                        Res.string.memory.string,
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                    Text(
                                        "$memoryUsedGb GB / $memoryTotalGb GB",
                                    )
                                    Text("${(memoryUsedGb / memoryTotalGb * 1000).roundToInt() / 10.0}%")
                                }
                            }
                        }
                        ElevatedCard(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            val swapUsedGb =
                                (systemInfo.memoryInfo.swapUsed / (1024 * 1024 * 1024.0) * 10).roundToInt() / 10.0
                            val swapTotalGb =
                                (systemInfo.memoryInfo.swapTotal / (1024 * 1024 * 1024.0) * 10).roundToInt() / 10.0
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    { (swapUsedGb / swapTotalGb).toFloat() },
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeWidth = 8.dp,
                                    modifier = Modifier
                                        .size(64.dp)
                                )
                                Column {
                                    Text(
                                        Res.string.swap.string,
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                    Text(
                                        "$swapUsedGb GB / $swapTotalGb GB",
                                    )
                                    Text("${(swapUsedGb / swapTotalGb * 1000).roundToInt() / 10.0}%")
                                }
                            }
                        }
                    }
                }
                items(systemInfo.fileSystemInfo.fileSystemList) {
                    OmmsServerStorageItem(it)
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
        IconButton(
            onClickButtonBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
        ) {
            Icon(Res.drawable.arrow_back_24px.painter, null)
        }
    }
}

@Composable
fun OmmsServerStorageItem(
    fileSystem: FileSystemInfo.FileSystem,
) {
    val storageTotalGb = (fileSystem.total / (1024*1024*1024.0) * 100).roundToInt() / 100.0
    val storageFreeGb = (fileSystem.free / (1024*1024*1024.0) * 100).roundToInt() / 100.0
    val storageUsedGb = storageTotalGb - storageFreeGb
    val storageUsedPercent = (storageUsedGb / storageTotalGb * 100 * 10).roundToInt() / 10.0
    ElevatedCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            CircularProgressIndicator(
                { ((storageTotalGb - storageFreeGb) / storageTotalGb).toFloat() },
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 8.dp,
                modifier = Modifier
                    .size(64.dp)
            )
            Column {
                Text(
                    fileSystem.mountPoint,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text("${Res.string.remain.string} $storageFreeGb GB")
                Text("${Res.string.total.string} $storageTotalGb GB")
                Text(
                    Res.string.used.string +
                        if (storageUsedPercent < 0.1) " < 0.1 %" else " $storageUsedPercent %"
                )
            }
        }
    }
}

@Composable
fun OmmsServerControllerItem(
    controller: Controller,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
    ) {
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

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun OmmsServerController(
    controller: Controller,
    onClickButtonBack: () -> Unit
) {
    var fetchControllerStatusState: FetchControllerStatusState by remember {
        mutableStateOf(FetchControllerStatusState.Fetching)
    }
    GlobalScope.launch {
        fetchControllerStatusFromServer(
            AppContainer.sessions[AppContainer.currentOmmsServerId!!]!!,
            controller.id
        ) {
            fetchControllerStatusState = it
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .wrapContentHeight()
                .width(550.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                ) {
                    Surface(
                        color = when (fetchControllerStatusState) {
                            is FetchControllerStatusState.Fetching ->
                                MaterialTheme.colorScheme.surfaceVariant
                            is FetchControllerStatusState.Error ->
                                MaterialTheme.colorScheme.error
                            is FetchControllerStatusState.Success -> {
                                val state = (fetchControllerStatusState as FetchControllerStatusState.Success)
                                if (state.status.isAlive) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    when (fetchControllerStatusState) {
                                        is FetchControllerStatusState.Fetching ->
                                            Res.drawable.refresh_24px.painter

                                        is FetchControllerStatusState.Success -> {
                                            val state =
                                                (fetchControllerStatusState as FetchControllerStatusState.Success)
                                            if (state.status.isAlive) {
                                                Res.drawable.check_circle_fill_24px.painter
                                            } else {
                                                Res.drawable.cancel_24px.painter
                                            }
                                        }

                                        is FetchControllerStatusState.Error ->
                                            Res.drawable.error_fill_24px.painter
                                    },
                                    null
                                )
                                Text(
                                    when (fetchControllerStatusState) {
                                        is FetchControllerStatusState.Fetching ->
                                            Res.string.status_waiting.string

                                        is FetchControllerStatusState.Success -> {
                                            val state =
                                                (fetchControllerStatusState as FetchControllerStatusState.Success)
                                            if (state.status.isAlive) {
                                                Res.string.label_state_running.string
                                            } else {
                                                Res.string.label_state_stopped.string
                                            }
                                        }

                                        is FetchControllerStatusState.Error ->
                                            Res.string.error.string
                                    }
                                )
                            }
                            Text(
                                controller.displayName,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                            )
                        }
                    }
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                Res.string.player_count.string,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            if (fetchControllerStatusState is FetchControllerStatusState.Success) {
                                val state = (fetchControllerStatusState as FetchControllerStatusState.Success)
                                if (!state.status.isAlive) {
                                    Text(Res.string.unavailable.string)
                                } else {
                                    Text("${state.status.playerCount} / ${state.status.maxPlayerCount}")
                                }
                            } else {
                                Text(Res.string.unavailable.string)
                            }
                            Text(
                                Res.string.player_list.string,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            if (fetchControllerStatusState is FetchControllerStatusState.Success) {
                                val state = (fetchControllerStatusState as FetchControllerStatusState.Success)
                                val players = state.status.players
                                if (!state.status.isAlive) {
                                    Text(Res.string.unavailable.string)
                                } else if (players.isEmpty()) {
                                    Text(Res.string.label_no_player.string)
                                }else for (player in players) {
                                    Text(player)
                                }
                            } else {
                                Text(Res.string.unavailable.string)
                            }
                        }
                    }
                }
            }
        }

        IconButton(
            onClickButtonBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
        ) {
            Icon(Res.drawable.arrow_back_24px.painter, null)
        }
    }
}
