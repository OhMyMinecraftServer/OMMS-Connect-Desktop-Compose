package cn.mercury9.omms.connect.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skiko.hostOs
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.*
import cn.mercury9.omms.connect.desktop.ui.theme.AppTheme
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeProvider
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import cn.mercury9.omms.connect.desktop.ui.window.about.AppUpdateScreen
import cn.mercury9.omms.connect.desktop.ui.window.main.MainScreen
import cn.mercury9.utils.compose.painter
import cn.mercury9.utils.compose.setMinimumSize
import cn.mercury9.utils.compose.string

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

    var showDialogWindowAppUpdate by remember { mutableStateOf(false) }

    ThemeProvider(appTheme) {
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

            AppMenuBar(
                appTheme,
                onShowWindowAppUpdateRequest = {
                    showDialogWindowAppUpdate = true
                }
            )

            MainScreen()
        } // end: Window

        if (showDialogWindowAppUpdate) {
            DialogWindow(
                state = rememberDialogState(
                    size = DpSize(404.dp, 303.dp)
                ),
                onCloseRequest = { showDialogWindowAppUpdate = false },
                title = Res.string.title_check_update.string
            ) {
                setMinimumSize(404.dp, 303.dp)

                AppUpdateScreen()
            }
        }
    }
}

@Composable
fun FrameWindowScope.AppMenuBar(
    appTheme: AppTheme,
    onShowWindowAppUpdateRequest: () -> Unit,
) {
    MenuBar {
        Menu(Res.string.title_settings.string) {
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
            Separator()
            Item(Res.string.title_check_update.string) {
                onShowWindowAppUpdateRequest()
            }
        }
    }
}
