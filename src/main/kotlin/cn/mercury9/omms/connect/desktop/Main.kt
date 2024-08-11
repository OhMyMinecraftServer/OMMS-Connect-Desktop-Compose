package cn.mercury9.omms.connect.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.mercury9.compose.utils.painter
import cn.mercury9.compose.utils.setMinimumSize
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.app_name
import cn.mercury9.omms.connect.desktop.resources.ic_launcher
import cn.mercury9.omms.connect.desktop.resources.option_setup_by_system_dark_theme
import cn.mercury9.omms.connect.desktop.resources.option_theme_dark
import cn.mercury9.omms.connect.desktop.resources.option_theme_light
import cn.mercury9.omms.connect.desktop.resources.title_activity_settings
import cn.mercury9.omms.connect.desktop.resources.title_settings_contrast
import cn.mercury9.omms.connect.desktop.resources.title_settings_dark
import cn.mercury9.omms.connect.desktop.resources.title_settings_theme
import cn.mercury9.omms.connect.desktop.ui.screen.MainScreen
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeProvider
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import org.jetbrains.compose.resources.stringResource

//private val ColorMacFullscreenGreen = Color(96, 197, 85)
//private val ColorMacOnFullscreenGreen = Color(80, 97, 24)
//private val ColorMacMinimizeYellow = Color(245, 190, 80)
//private val ColorMacOnMinimizeYellow = Color(142, 90, 29)
//private val ColorMacCloseRed = Color(237, 106, 94)
//private val ColorMacOnCloseRed = Color(140, 26, 16)
//private const val MacButtonSize = 12

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

    Window(
        onCloseRequest = {
            for (session in AppContainer.sessions.values) {
                session.close{}
            }
            exitApplication()
        },
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
        setMinimumSize(916.dp, 687.dp)
//        fun close() {
//            for (session in AppContainer.sessions.values) {
//                session.close{}
//            }
//            exitApplication()
//        }
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
            Surface(
//                shape = when (windowState.placement) {
//                    WindowPlacement.Fullscreen ->
//                        RoundedCornerShape(0.dp)
//                    else ->
//                        MaterialTheme.shapes.medium
//                }
            ) {
                Column {
//                    // 计划自定义窗口标题栏，但没有完全成功
//                    WindowDraggableArea {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .wrapContentHeight()
//                                .background(MaterialTheme.colorScheme.background)
//                        ) {
//                            if (hostOs.isMacOS) {
//                                val interactionSourceButtons = remember { MutableInteractionSource() }
//                                val hoverOnButtons by interactionSourceButtons.collectIsHoveredAsState()
//                                Row(
//                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                                    modifier = Modifier
//                                        .align(Alignment.CenterStart)
//                                        .padding(14.dp)
//                                        .hoverable(interactionSourceButtons)
//                                ) {
//                                    Surface(
//                                        shape = CircleShape,
//                                        color = ColorMacCloseRed,
//                                        modifier =  Modifier
//                                            .size(MacButtonSize.dp)
//                                            .clickable { close() }
//                                    ) {
//                                        if (hoverOnButtons) {
//                                            Icon(
//                                                Res.drawable.close_20px.painter,
//                                                null,
//                                                tint = ColorMacOnCloseRed,
//                                                modifier = Modifier
//                                                    .size(10.dp)
//                                                    .padding(1.dp)
//                                            )
//                                        }
//                                    }
//                                    Surface(
//                                        shape = CircleShape,
//                                        color = ColorMacMinimizeYellow,
//                                        modifier =  Modifier
//                                            .size(MacButtonSize.dp)
//                                            .clickable { minimize() }
//                                    ) {
//                                        if (hoverOnButtons) {
//                                            Icon(
//                                                Res.drawable.remove_20px.painter,
//                                                null,
//                                                tint = ColorMacOnMinimizeYellow,
//                                                modifier = Modifier
//                                                    .size(10.dp)
//                                                    .padding(1.dp)
//                                            )
//                                        }
//                                    }
//                                    Surface(
//                                        shape = CircleShape,
//                                        color = ColorMacFullscreenGreen,
//                                        modifier =  Modifier
//                                            .size(MacButtonSize.dp)
//                                            .clickable { switchFullscreen() }
//                                    ) {
//                                        if (hoverOnButtons) {
//                                            Icon(
//                                                if (windowState.placement != WindowPlacement.Maximized)
//                                                    Res.drawable.open_in_full_20px.painter
//                                                else
//                                                    Res.drawable.close_fullscreen_20px.painter,
//                                                null,
//                                                tint = ColorMacOnFullscreenGreen,
//                                                modifier = Modifier
//                                                    .size(10.dp)
//                                                    .padding(1.dp)
//                                            )
//                                        }
//                                    }
//                                }
//                            } else {
//                                Row(
//                                    modifier = Modifier
//                                        .align(Alignment.CenterEnd)
//                                ) {
//                                    IconButton(::minimize) {
//                                        Icon(Res.drawable.minimize_24px.painter, null)
//                                    }
//                                    IconButton(::switchFullscreen) {
//                                        Icon(Res.drawable.fullscreen_24px.painter, null)
//                                    }
//                                    IconButton(::close) {
//                                        Icon(Res.drawable.close_24px.painter, null)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    HorizontalDivider()
                    MainScreen()
                }
            }
        }
    }
}
