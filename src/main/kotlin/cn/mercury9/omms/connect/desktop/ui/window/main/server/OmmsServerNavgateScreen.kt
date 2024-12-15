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
import cn.mercury9.omms.connect.desktop.resources.title_console
import cn.mercury9.omms.connect.desktop.resources.title_server
import cn.mercury9.omms.connect.desktop.resources.title_whitelist
import cn.mercury9.omms.connect.desktop.ui.component.LongPressIconButton
import cn.mercury9.utils.compose.painter
import cn.mercury9.utils.compose.string
import kotlin.time.Duration.Companion.seconds

data object OmmsServerNavRoute {
    const val CONTROLLERS_SCREEN = "CONTROLLERS_SCREEN"
    const val WHITELIST_SCREEN = "WHITELIST_SCREEN"
    const val CHAT_SCREEN = "CHAT_SCREEN"
    const val CONSOLE_SCREEN = "CONSOLE_SCREEN"
}

data class NavigationTarget(
    val navRoute: String,
    val name: String,
    val icon: Painter,
)

@Composable
fun OmmsServerNavigateScreen() {
    val navController = rememberNavController()
    AppContainer.navController = navController
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
            composable(OmmsServerNavRoute.CHAT_SCREEN) {
                OmmsChatScreen()
            }
            composable(OmmsServerNavRoute.CONSOLE_SCREEN) {
                OmmsConsoleScreen()
            }
        }
    }
}

@Composable
fun OmmsServerScreenTopBar(
    navController: NavHostController,
) {
    val serverName = AppContainer.sessions[AppContainer.currentOmmsServerId]?.serverName

    fun exitServer() {
        val id = AppContainer.currentOmmsServerId ?: return
        val session = AppContainer.sessions[id] ?: return
        endOmmsServerConnection(session) {
            try {
                navController.clearBackStack(OmmsServerNavRoute.CONTROLLERS_SCREEN)
            } catch (_: Throwable) {}
            AppContainer.sessions.remove(id)
            AppContainer.currentOmmsServerId = null
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
                text = "${AppContainer.servers[AppContainer.currentOmmsServerId]?.name} ( $serverName )",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        OmmsServerScreenTopBarNavigateButtons(
            navController,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        )
    }
}

@Composable
fun OmmsServerScreenTopBarNavigateButtons(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navigationTargets = listOf(
        NavigationTarget(
            OmmsServerNavRoute.CONTROLLERS_SCREEN,
            Res.string.title_server.string,
            Res.drawable.monitor_24px.painter
        ),
        NavigationTarget(
            OmmsServerNavRoute.WHITELIST_SCREEN,
            Res.string.title_whitelist.string,
            Res.drawable.sensor_window_24px.painter
        ),
        NavigationTarget(
            OmmsServerNavRoute.CHAT_SCREEN,
            Res.string.title_chat.string,
            Res.drawable.chat_24px.painter
        ),
        NavigationTarget(
            OmmsServerNavRoute.CONSOLE_SCREEN,
            Res.string.title_console.string,
            Res.drawable.monitor_24px.painter
        )
    )
    var current by remember { mutableStateOf(OmmsServerNavRoute.CONTROLLERS_SCREEN) }
    fun navigate(route: String) {
        navController.navigate(route)
        current = route
    }
    Row (
        modifier = modifier
                .size((80 * navigationTargets.size).dp, 64.dp),
    ) {
        for (target in navigationTargets) {
            NavigationBarItem(
                selected = current == target.navRoute,
                onClick = {
                    navigate(target.navRoute)
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
