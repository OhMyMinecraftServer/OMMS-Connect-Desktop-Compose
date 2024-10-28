package cn.mercury9.omms.connect.desktop

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.app_name
import cn.mercury9.omms.connect.desktop.resources.ic_launcher
import cn.mercury9.omms.connect.desktop.resources.option_setup_by_system_dark_theme
import cn.mercury9.omms.connect.desktop.resources.option_theme_dark
import cn.mercury9.omms.connect.desktop.resources.option_theme_light
import cn.mercury9.omms.connect.desktop.resources.title_check_update
import cn.mercury9.omms.connect.desktop.resources.title_settings
import cn.mercury9.omms.connect.desktop.resources.title_settings_contrast
import cn.mercury9.omms.connect.desktop.resources.title_settings_dark
import cn.mercury9.omms.connect.desktop.resources.title_settings_theme
import cn.mercury9.omms.connect.desktop.ui.component.EasyDropdownMenu
import cn.mercury9.omms.connect.desktop.ui.theme.AppTheme
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeProvider
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import cn.mercury9.omms.connect.desktop.ui.window.about.AppUpdateScreen
import cn.mercury9.omms.connect.desktop.ui.window.main.MainScreen
import cn.mercury9.utils.compose.painter
import cn.mercury9.utils.compose.setMinimumSize
import cn.mercury9.utils.compose.string
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.intui.standalone.theme.createEditorTextStyle
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.defaultTitleBarStyle
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.TitleBarColors
import org.jetbrains.jewel.window.styling.TitleBarStyle
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

    val textStyle = JewelTheme.createDefaultTextStyle()
    val editorStyle = JewelTheme.createEditorTextStyle()

    val themeDefinition =
        if (appTheme.darkTheme) {
            JewelTheme.darkThemeDefinition(defaultTextStyle = textStyle, editorTextStyle = editorStyle)
        } else {
            JewelTheme.lightThemeDefinition(defaultTextStyle = textStyle, editorTextStyle = editorStyle)
        }

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
        IntUiTheme(
            theme = themeDefinition,
            styling = ComponentStyling.default()
                .decoratedWindow(
                    titleBarStyle =
                    if (appTheme.darkTheme) TitleBarStyle.dark()
                    else TitleBarStyle.light(),
                )
        ) {
            DecoratedWindow(
                onCloseRequest = ::onCloseRequest,
                state = windowState,
                title = stringResource(Res.string.app_name),
                icon = Res.drawable.ic_launcher.painter,
                onKeyEvent = {
                    var flag = false
                    for (handler in AppContainer.onKeyEvent.values) {
                        if (handler(it)) flag = true
                    }
                    return@DecoratedWindow flag
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

                AppTitleBar()

                Column {
                    AppMenuBar(
                        appTheme,
                        onShowWindowAppUpdateRequest = {
                            showDialogWindowAppUpdate = true
                        }
                    )
                    MainScreen()
                }
            } // end: Window

            if (showDialogWindowAppUpdate) {
                DecoratedWindow(
                    state = rememberWindowState(
                        size = DpSize(404.dp, 303.dp)
                    ),
                    onCloseRequest = { showDialogWindowAppUpdate = false },
                    title = Res.string.title_check_update.string
                ) {
                    setMinimumSize(404.dp, 303.dp)

                    AppTitleBar()

                    AppUpdateScreen()
                }
            }
        }
    }
}

@Composable
fun FrameWindowScope.AppMenuBar(
    appTheme: AppTheme,
    onShowWindowAppUpdateRequest: () -> Unit,
) {
    if (hostOs.isWindows) {
        Surface {
            Column {
                AppMenuBarWindows(appTheme, onShowWindowAppUpdateRequest)
                HorizontalDivider()
            }
        }
    } else {
        AppMenuBarNative(appTheme, onShowWindowAppUpdateRequest)
    }
}

@Composable
private fun FrameWindowScope.AppMenuBarNative(
    appTheme: AppTheme,
    onShowWindowAppUpdateRequest: () -> Unit
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

@Composable
private fun AppMenuBarWindows(
    appTheme: AppTheme,
    onShowWindowAppUpdateRequest: () -> Unit,
) {
    Row {
        EasyDropdownMenu({
            Text(Res.string.title_settings.string)
        }) {
            SubMenu({
                Text(Res.string.title_settings_theme.string)
            }) {
                SubMenu({
                    Text(Res.string.title_settings_theme.string)
                }) {
                    for (themeType in ThemeType.entries) {
                        RadioButtonItem(
                            text = { Text(themeType.name) },
                            selected = appTheme.themeType == themeType,
                        ) {
                            AppContainer.config.get().apply {
                                theme.themeType = themeType
                            }.also {
                                AppContainer.config.set(it)
                            }
                        }
                    }
                }
                SubMenu({
                    Text(Res.string.title_settings_dark.string)
                }) {
                    CheckboxItem(
                        text = { Text(Res.string.option_setup_by_system_dark_theme.string) },
                        checked = {
                            AppContainer.config.get().setupThemeBySystemDarkTheme
                        },
                    ) { checked ->
                        AppContainer.config.get().apply {
                            setupThemeBySystemDarkTheme = checked
                        }.also { config ->
                            AppContainer.config.set(config)
                        }
                    }
                    Divider()
                    RadioButtonItem(
                        text = { Text(Res.string.option_theme_light.string) },
                        selected = !appTheme.darkTheme,
                    ) {
                        AppContainer.config.get().apply {
                            theme.darkTheme = false
                        }.also {
                            AppContainer.config.set(it)
                        }
                    }
                    RadioButtonItem(
                        text = { Text(Res.string.option_theme_dark.string) },
                        selected = appTheme.darkTheme,
                    ) {
                        AppContainer.config.get().apply {
                            theme.darkTheme = true
                        }.also {
                            AppContainer.config.set(it)
                        }
                    }
                }
                SubMenu({
                    Text(Res.string.title_settings_contrast.string)
                }) {
                    for (contrastType in ContrastType.entries) {
                        RadioButtonItem(
                            text = { Text(contrastType.name) },
                            selected = appTheme.contrastType == contrastType,
                        ) {
                            AppContainer.config.get().apply {
                                theme.contrastType = contrastType
                            }.also {
                                AppContainer.config.set(it)
                            }
                        }
                    }
                }
            }
            Divider()
            ButtonItem(
                text = { Text(Res.string.title_check_update.string) },
            ) {
                onShowWindowAppUpdateRequest()
            }
        }
    }
}

@Composable
fun DecoratedWindowScope.AppTitleBar() {
    val backgroundColor by animateColorAsState(MaterialTheme.colorScheme.background)
    val outlineColor by animateColorAsState(MaterialTheme.colorScheme.outline)
    TitleBar(
        style = TitleBarStyle(
            colors = TitleBarColors(
                background = backgroundColor,
                inactiveBackground = backgroundColor,
                content = backgroundColor,
                border = outlineColor,
                fullscreenControlButtonsBackground = backgroundColor,
                titlePaneButtonHoveredBackground = backgroundColor,
                titlePaneButtonPressedBackground = backgroundColor,
                titlePaneCloseButtonHoveredBackground = backgroundColor,
                titlePaneCloseButtonPressedBackground = backgroundColor,
                iconButtonHoveredBackground = backgroundColor,
                iconButtonPressedBackground = backgroundColor,
                dropdownPressedBackground = backgroundColor,
                dropdownHoveredBackground = backgroundColor
            ),
            metrics = JewelTheme.defaultTitleBarStyle.metrics,
            icons = JewelTheme.defaultTitleBarStyle.icons,
            dropdownStyle = JewelTheme.defaultTitleBarStyle.dropdownStyle,
            iconButtonStyle = JewelTheme.defaultTitleBarStyle.iconButtonStyle,
            paneButtonStyle = JewelTheme.defaultTitleBarStyle.paneButtonStyle,
            paneCloseButtonStyle = JewelTheme.defaultTitleBarStyle.paneCloseButtonStyle
        ),
        modifier = Modifier
            .newFullscreenControls(),
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title)
            }
        }
    }
}
