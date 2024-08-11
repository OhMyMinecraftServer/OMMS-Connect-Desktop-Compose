package cn.mercury9.omms.connect.desktop.ui.screen.server

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.mercury9.compose.utils.painter
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.client.endOmmsServerConnection
import cn.mercury9.omms.connect.desktop.client.getServerName
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.logout
import cn.mercury9.omms.connect.desktop.resources.logout_24px
import cn.mercury9.omms.connect.desktop.resources.monitor_24px
import cn.mercury9.omms.connect.desktop.resources.notifications_24px
import cn.mercury9.omms.connect.desktop.resources.sensor_window_24px
import cn.mercury9.omms.connect.desktop.resources.title_broadcast
import cn.mercury9.omms.connect.desktop.resources.title_server
import cn.mercury9.omms.connect.desktop.resources.title_whitelist

data object OmmsServerNavRoute {
    const val CONTROLLERS_SCREEN = "CONTROLLERS_SCREEN"
    const val WHITELIST_SCREEN = "WHITELIST_SCREEN"
    const val ANNOUNCEMENT_SCREEN = "ANNOUNCEMENT_SCREEN"
}

@Composable
fun OmmsServerNavigateScreen() {
    val navController = rememberNavController()
    Column {
        OmmsServerScreenTopBar(navController)
        HorizontalDivider()
        NavHost(
            navController,
            startDestination = OmmsServerNavRoute.CONTROLLERS_SCREEN
        ) {
            composable(OmmsServerNavRoute.CONTROLLERS_SCREEN) {
                OmmsControllersScreen()
            }
            composable(OmmsServerNavRoute.WHITELIST_SCREEN) {
                OmmsWhitelistScreen()
            }
            composable(OmmsServerNavRoute.ANNOUNCEMENT_SCREEN) {
                Text("TODO")
            }
        }
    }
}

@Composable
fun OmmsServerScreenTopBar(
    navController: NavHostController,
) {
    val serverName = getServerName(AppContainer.sessions[AppContainer.currentOmmsServerId])
    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            IconButton({
                val id = AppContainer.currentOmmsServerId!!
                val session = AppContainer.sessions[id]
                endOmmsServerConnection(session) {
                    try {
                        navController.clearBackStack(OmmsServerNavRoute.CONTROLLERS_SCREEN)
                    } catch (_: Throwable) {}
                    AppContainer.sessions.remove(id)
                    AppContainer.currentOmmsServerId = null
                }
            }) {
                Icon(
                    Res.drawable.logout_24px.painter,
                    Res.string.logout.string
                )
            }
            Text(
                text = "${AppContainer.servers.get()[AppContainer.currentOmmsServerId]?.name} ( $serverName )",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        OmmsServerScreenTopBarNavigateButtons(
            navController,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(192.dp, 64.dp)
                .padding(end = 8.dp)
        )
    }
}

@Composable
fun OmmsServerScreenTopBarNavigateButtons(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    var current by remember { mutableStateOf(OmmsServerNavRoute.CONTROLLERS_SCREEN) }
    fun navigate(route: String) {
        navController.navigate(route)
        current = route
    }
    Row (
        modifier = modifier,
    ) {
        NavigationBarItem(
            modifier = Modifier.size(64.dp),
            selected = current == OmmsServerNavRoute.CONTROLLERS_SCREEN,
            onClick = {
                navigate(OmmsServerNavRoute.CONTROLLERS_SCREEN)
            },
            icon = {
                Icon(
                    Res.drawable.monitor_24px.painter,
                    Res.string.title_server.string
                )
            },
            label = {
                Text(Res.string.title_server.string)
            }
        )
        NavigationBarItem(
            modifier = Modifier.size(64.dp),
            selected = current == OmmsServerNavRoute.WHITELIST_SCREEN,
            onClick = {
                navigate(OmmsServerNavRoute.WHITELIST_SCREEN)
            },
            icon = {
                Icon(
                    Res.drawable.sensor_window_24px.painter,
                    Res.string.title_whitelist.string
                )
            },
            label = {
                Text(Res.string.title_whitelist.string)
            }
        )
        NavigationBarItem(
            modifier = Modifier.size(64.dp),
            selected = current == OmmsServerNavRoute.ANNOUNCEMENT_SCREEN,
            onClick = {
                navigate(OmmsServerNavRoute.ANNOUNCEMENT_SCREEN)
            },
            icon = {
                Icon(
                    Res.drawable.notifications_24px.painter,
                    Res.string.title_broadcast.string
                )
            },
            label = {
                Text(Res.string.title_broadcast.string)
            }
        )
    }
}
