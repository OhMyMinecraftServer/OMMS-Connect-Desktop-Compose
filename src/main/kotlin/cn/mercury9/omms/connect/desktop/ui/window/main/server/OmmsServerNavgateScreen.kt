package cn.mercury9.omms.connect.desktop.ui.window.main.server

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import cn.mercury9.omms.connect.desktop.client.omms.endOmmsServerConnection
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.*
import cn.mercury9.omms.connect.desktop.ui.component.LongPressIconButton
import cn.mercury9.utils.compose.painter
import cn.mercury9.utils.compose.string

data object OmmsServerNavRoute {
    const val CONTROLLERS_SCREEN = "CONTROLLERS_SCREEN"
    const val WHITELIST_SCREEN = "WHITELIST_SCREEN"
    const val CHAT_SCREEN = "CHAT_SCREEN"
    const val ANNOUNCEMENT_SCREEN = "ANNOUNCEMENT_SCREEN"
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
            composable(OmmsServerNavRoute.ANNOUNCEMENT_SCREEN) {
                OmmsAnnouncementScreen()
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

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressResponseTime = 1.seconds
    var pressedTime by remember { mutableStateOf(0.milliseconds) }
    var lastPressedInstant by remember { mutableStateOf(null as Instant?) }

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

    if (isPressed) {
        val now = Clock.System.now()
        if (pressedTime < pressResponseTime) {
            lastPressedInstant?.let {
                val delta = now - it
                pressedTime += delta
            }
        } else {
            exitServer()
        }
        lastPressedInstant = now

    } else if (pressedTime < 0.milliseconds) {
        pressedTime = 0.milliseconds
        lastPressedInstant = null
    } else if (pressedTime > 0.milliseconds) {
        val now = Clock.System.now()
        lastPressedInstant?.let {
            val delta = now - it
            pressedTime -= delta * 2
        }
        lastPressedInstant = now
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
//            IconButton(
//                onClick = {},
//                interactionSource = interactionSource,
//            ) {
//                Box {
//                    CircularProgressIndicator(
//                        trackColor = Color.Transparent,
//                        progress = {
//                            (pressedTime / pressResponseTime).toFloat()
//                        }
//                    )
//                    Icon(
//                        Res.drawable.logout_24px.painter,
//                        Res.string.logout.string,
//                        modifier = Modifier
//                            .align(Alignment.Center)
//                    )
//                }
//            }
            Text(
                text = "${AppContainer.servers.get()[AppContainer.currentOmmsServerId]?.name} ( $serverName )",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        OmmsServerScreenTopBarNavigateButtons(
            navController,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size((80*4).dp, 64.dp)
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
            OmmsServerNavRoute.ANNOUNCEMENT_SCREEN,
            Res.string.title_broadcast.string,
            Res.drawable.notifications_24px.painter
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
        modifier = modifier,
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
