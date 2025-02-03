package cn.mercury9.omms.connect.desktop.ui.window.main.server

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.mercury9.omms.connect.desktop.client.omms.endOmmsServerConnection
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.chat_24px
import cn.mercury9.omms.connect.desktop.resources.logout
import cn.mercury9.omms.connect.desktop.resources.logout_24px
import cn.mercury9.omms.connect.desktop.resources.monitor_24px
import cn.mercury9.omms.connect.desktop.resources.sensor_window_24px
import cn.mercury9.omms.connect.desktop.resources.title_chat
import cn.mercury9.omms.connect.desktop.resources.title_server
import cn.mercury9.omms.connect.desktop.resources.title_whitelist
import cn.mercury9.omms.connect.desktop.ui.component.LongPressIconButton
import cn.mercury9.omms.connect.desktop.ui.window.main.server.chat.OmmsChatScreen
import cn.mercury9.omms.connect.desktop.ui.window.main.server.controller.OmmsControllersScreen
import cn.mercury9.omms.connect.desktop.ui.window.main.server.whitelist.OmmsWhitelistScreen
import cn.mercury9.utils.compose.painter
import cn.mercury9.utils.compose.string
import kotlin.time.Duration.Companion.seconds

data class NavigationTarget(
    val name: String,
    val icon: Painter,
    val composable: @Composable () -> Unit
)

enum class OmmsServerNavRoute {
    CONTROLLERS_SCREEN,
    WHITELIST_SCREEN,
    CHAT_SCREEN,
    ;
    companion object {
        @Composable
        fun getTarget(route: OmmsServerNavRoute): NavigationTarget {
            return when (route) {
                CONTROLLERS_SCREEN -> NavigationTarget(
                    Res.string.title_server.string,
                    Res.drawable.monitor_24px.painter,
                ) {
                    OmmsControllersScreen()
                }

                WHITELIST_SCREEN -> NavigationTarget(
                    Res.string.title_whitelist.string,
                    Res.drawable.sensor_window_24px.painter,
                ) {
                    OmmsWhitelistScreen()
                }

                CHAT_SCREEN -> NavigationTarget(
                    Res.string.title_chat.string,
                    Res.drawable.chat_24px.painter,
                ) {
                    OmmsChatScreen()
                }
            }
        }
    }
}

@Composable
fun OmmsServerNavigateScreen() {
    val navController = rememberNavController()
    AppContainer.navController = navController
    Column {
        OmmsServerScreenTopBar(navController)
        HorizontalDivider()
        NavHost(
            navController,
            startDestination = OmmsServerNavRoute.CONTROLLERS_SCREEN.name
        ) {
            OmmsServerNavRoute.entries.forEach { route ->
                composable(route.name) {
                    OmmsServerNavRoute.getTarget(route).composable()
                }
            }
        }
    }
}

@Composable
fun OmmsServerScreenTopBar(
    navController: NavHostController,
) {
    val serverName = AppContainer.currentOmmsServerSession?.serverName

    fun exitServer() {
        val id by AppContainer.currentOmmsServerId
        id ?: return
        val session = AppContainer.sessions[id] ?: return
        endOmmsServerConnection(session) {
            navController.clearBackStack(OmmsServerNavRoute.CONTROLLERS_SCREEN.name)
            AppContainer.sessions.remove(id)
            AppContainer.currentOmmsServerId.value = null
        }
    }

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
            LongPressIconButton(
                responseTime = 1.seconds,
                onClick = { exitServer() },
            ) {
                Icon(
                    Res.drawable.logout_24px.painter,
                    Res.string.logout.string,
                )
            }
            Text(
                text = "${AppContainer.servers[AppContainer.currentOmmsServerId.value]?.name} ( $serverName )",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        OmmsServerScreenNavigationBar(
            navController,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        )
    }
}

@Composable
fun OmmsServerScreenNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    var current by remember { mutableStateOf(OmmsServerNavRoute.CONTROLLERS_SCREEN) }
    fun navigate(route: OmmsServerNavRoute) {
        navController.navigate(route.name)
        current = route
    }
    Row (
        modifier = modifier
                .size((80 * OmmsServerNavRoute.entries.size).dp, 64.dp),
    ) {
        OmmsServerNavRoute.entries.forEach { route ->
            val target = OmmsServerNavRoute.getTarget(route)
            NavigationBarItem(
                selected = current == route,
                onClick = {
                    navigate(route)
                },
                icon = {
                    Icon(
                        target.icon,
                        target.name
                    )
                },
                label = {
                    Text(target.name)
                }
            )
        }
    }
}
