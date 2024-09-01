package cn.mercury9.omms.connect.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.*
import cn.mercury9.omms.connect.desktop.ui.screen.MainScreen
import cn.mercury9.omms.connect.desktop.ui.theme.AppTheme
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeProvider
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import cn.mercury9.utils.compose.painter
import cn.mercury9.utils.compose.setMinimumSize
import cn.mercury9.utils.compose.string
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skiko.hostOs
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1200.dp, 900.dp)
    )
    AppContainer.mainWindowState = windowState

    val isSystemInDarkTheme = isSystemInDarkTheme()
    var appTheme by remember { mutableStateOf(
        AppContainer.config.get().theme.appTheme().apply {
            if (AppContainer.config.get().setupThemeBySystemDarkTheme) {
                darkTheme = isSystemInDarkTheme
            }
        }
    ) }

    AppContainer.config.onConfigChange += "Main_AppTheme" to {
        appTheme = AppContainer.config.get().theme.appTheme()
    }

    fun onCloseRequest() {
        for (session in AppContainer.sessions.values) {
            session.close {}
        }
        exitApplication()
    }

    Window(
        onCloseRequest = ::onCloseRequest,
        state = windowState,
        title = stringResource(Res.string.app_name),
        icon = Res.drawable.ic_launcher.painter,
        onKeyEvent = {
            var flag = false
            for (handler in AppContainer.onKeyEvent.values) {
                if (handler(it)) flag = true
            }
            return@Window flag
        },
//        undecorated = true,
//        transparent = true
    ) {
        setMinimumSize(916.dp, 687.dp)

        if (hostOs.isWindows) {     // 移动窗口时吸附屏幕边缘
            with(LocalDensity.current) {
                LaunchedEffect(windowState.position) {
                    val screenSizePx = Toolkit.getDefaultToolkit().screenSize

                    // 屏幕空间被系统占用的部分
                    val screenInsetsPx = Toolkit.getDefaultToolkit().getScreenInsets(
                        GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration
                    )

                    val windowAbsorbDistance = 20f

                    // position.x = 0 不会贴边，似乎是因为系统加窗口阴影也要算上，除了上边，都有这个问题
                    val windowPositionOffsetPx = 7f

                    // Left
                    if ((windowState.position.x.value
                                + windowPositionOffsetPx.toDp().value
                        ) in (
                            (-windowAbsorbDistance + screenInsetsPx.left.toDp().value)
                            ..(windowAbsorbDistance + screenInsetsPx.left.toDp().value)
                        )
                    ) {
                        val position = windowState.position as WindowPosition.Absolute
                        windowState.position = position.copy(x = -windowPositionOffsetPx.toDp())
                    }

                    // Top
                    if (windowState.position.y.value
                        in (
                            (-windowAbsorbDistance + screenInsetsPx.top.toDp().value)
                            ..(windowAbsorbDistance + screenInsetsPx.top.toDp().value)
                        )
                    ) {
                        val position = windowState.position as WindowPosition.Absolute
                        windowState.position = position.copy(y = 0.dp)
                    }

                    // Right
                    if ((windowState.position.x.value
                                + windowPositionOffsetPx.toDp().value
                                + windowState.size.width.value
                        ) in (
                            (-windowAbsorbDistance
                                + screenSizePx.width.toDp().value
                                - screenInsetsPx.right.toDp().value
                            )..(windowAbsorbDistance
                                + screenSizePx.width.toDp().value
                                + screenInsetsPx.right.toDp().value
                            )
                        )
                    ) {
                        val position = windowState.position as WindowPosition.Absolute
                        windowState.position = position.copy(
                            x = screenSizePx.width.toDp() - windowState.size.width
                                    + windowPositionOffsetPx.toDp()
                        )
                    }

                    // Bottom
                    if ((windowState.position.y.value
                                + windowPositionOffsetPx.toDp().value
                                + windowState.size.height.value
                        ) in (
                            (-windowAbsorbDistance
                                + screenSizePx.height.toDp().value
                                - screenInsetsPx.bottom.toDp().value
                            )..(windowAbsorbDistance
                                + screenSizePx.height.toDp().value
                                + screenInsetsPx.bottom.toDp().value
                            )
                        )
                    ) {
                        val position = windowState.position as WindowPosition.Absolute
                        windowState.position = position.copy(
                            y = screenSizePx.height.toDp() - windowState.size.height
                                    + windowPositionOffsetPx.toDp()
                        )
                    }
                }
            }
        } // End: 移动窗口时吸附屏幕边缘

        AppMenuBar(appTheme)

//        fun minimize() {
//            windowState.isMinimized = true
//        }
//        fun switchFullscreen() {
//            if (windowState.placement != WindowPlacement.Maximized) {
//                windowState.placement = WindowPlacement.Maximized
//            } else {
//                windowState.placement = WindowPlacement.Floating
//            }
//        }

        ThemeProvider(appTheme) {
//            Surface(
//                shape = when (windowState.placement) {
//                    WindowPlacement.Fullscreen ->
//                        RoundedCornerShape(0.dp)
//                    else ->
//                        MaterialTheme.shapes.medium
//                }
//            ) {
//                Column {
//                        // 计划自定义窗口标题栏，但没有完全成功
//                        AppDraggableArea(::onCloseRequest, ::minimize, ::switchFullscreen, windowState)
//                        HorizontalDivider()
                    MainScreen()
//                }
//            }
        }
    }
}

@Composable
fun FrameWindowScope.AppMenuBar(
    appTheme: AppTheme
) {
    MenuBar {
        Menu(Res.string.title_activity_settings.string) {
            Menu(Res.string.title_settings_theme.string) {
                Menu(Res.string.title_settings_theme.string) {
                    for (themeType in ThemeType.entries) {
                        RadioButtonItem(
                            themeType.name,
                            themeType == appTheme.themeType
                        ) {
                            AppContainer.config.get().apply {
                                theme.themeType = themeType
                            }.also {
                                AppContainer.config.set(it)
                            }
                        }
                    }
                }
                Menu(Res.string.title_settings_dark.string) {
                    CheckboxItem(
                        Res.string.option_setup_by_system_dark_theme.string,
                        AppContainer.config.get().setupThemeBySystemDarkTheme
                    ) {
                        AppContainer.config.get().apply {
                            setupThemeBySystemDarkTheme = it
                        }.also {
                            AppContainer.config.set(it)
                        }
                    }
                    Separator()
                    RadioButtonItem(
                        Res.string.option_theme_light.string,
                        !appTheme.darkTheme
                    ) {
                        AppContainer.config.get().apply {
                            theme.darkTheme = false
                        }.also {
                            AppContainer.config.set(it)
                        }
                    }
                    RadioButtonItem(
                        Res.string.option_theme_dark.string,
                        appTheme.darkTheme
                    ) {
                        AppContainer.config.get().apply {
                            theme.darkTheme = true
                        }.also {
                            AppContainer.config.set(it)
                        }
                    }
                }
                Menu(Res.string.title_settings_contrast.string) {
                    for (contrast in ContrastType.entries) {
                        RadioButtonItem(
                            contrast.name,
                            contrast == appTheme.contrastType
                        ) {
                            AppContainer.config.get().apply {
                                theme.contrastType = contrast
                            }.also {
                                AppContainer.config.set(it)
                            }
                        }
                    }
                }
            } // end: settings - theme

        }
    }
}

@Composable
fun FrameWindowScope.AppDraggableArea(
    close: () -> Unit,
    minimize: () -> Unit,
    switchFullscreen: () -> Unit,
    windowState: WindowState,
) {
    val colorMacFullscreenGreen = Color(96, 197, 85)
    val colorMacOnFullscreenGreen = Color(80, 97, 24)
    val colorMacMinimizeYellow = Color(245, 190, 80)
    val colorMacOnMinimizeYellow = Color(142, 90, 29)
    val colorMacCloseRed = Color(237, 106, 94)
    val colorMacOnCloseRed = Color(140, 26, 16)
    val macButtonSize = 12
    WindowDraggableArea {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (hostOs.isMacOS) {
                val interactionSourceButtons = remember { MutableInteractionSource() }
                val hoverOnButtons by interactionSourceButtons.collectIsHoveredAsState()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(14.dp)
                        .hoverable(interactionSourceButtons)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = colorMacCloseRed,
                        modifier =  Modifier
                            .size(macButtonSize.dp)
                            .clickable { close() }
                    ) {
                        if (hoverOnButtons) {
                            Icon(
                                Res.drawable.close_20px.painter,
                                null,
                                tint = colorMacOnCloseRed,
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(1.dp)
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = colorMacMinimizeYellow,
                        modifier =  Modifier
                            .size(macButtonSize.dp)
                            .clickable { minimize() }
                    ) {
                        if (hoverOnButtons) {
                            Icon(
                                Res.drawable.remove_20px.painter,
                                null,
                                tint = colorMacOnMinimizeYellow,
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(1.dp)
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = colorMacFullscreenGreen,
                        modifier =  Modifier
                            .size(macButtonSize.dp)
                            .clickable { switchFullscreen() }
                    ) {
                        if (hoverOnButtons) {
                            Icon(
                                if (windowState.placement != WindowPlacement.Maximized)
                                    Res.drawable.open_in_full_20px.painter
                                else
                                    Res.drawable.close_fullscreen_20px.painter,
                                null,
                                tint = colorMacOnFullscreenGreen,
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(1.dp)
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    IconButton(minimize) {
                        Icon(Res.drawable.minimize_24px.painter, null)
                    }
                    IconButton(switchFullscreen) {
                        Icon(Res.drawable.fullscreen_24px.painter, null)
                    }
                    IconButton(close) {
                        Icon(Res.drawable.close_24px.painter, null)
                    }
                }
            }
        }
    }
}
