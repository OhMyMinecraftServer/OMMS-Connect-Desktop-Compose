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
import cn.mercury9.omms.connect.desktop.client.fetchControllersFormServer
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.ic_server_default
import cn.mercury9.omms.connect.desktop.resources.ic_server_fabric
import cn.mercury9.omms.connect.desktop.resources.label_controller_type
import icu.takeneko.omms.client.data.controller.Controller
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun OmmsControllersScreen() {
    var fetchControllersState: FetchControllersState by remember { mutableStateOf(FetchControllersState.Fetching) }
    GlobalScope.launch {
        fetchControllersFormServer(
            AppContainer.sessions[AppContainer.currentOmmsServerId!!]!!
        ) {
            fetchControllersState = it
        }
    }
    AnimatedVisibility(
        fetchControllersState is FetchControllersState.Success,
        enter = slideIn {
            IntOffset(0, -it.height)
        }
    ) {
       OmmsServerControllerList((fetchControllersState as FetchControllersState.Success).controllers)
    }
}

@Composable
fun OmmsServerControllerList(
    controllers: Map<String, Controller>
) {
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
            items(
                controllers.values.toList(),
                key = { it.id }
            ) {
                OmmsServerControllerItem(it)
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
