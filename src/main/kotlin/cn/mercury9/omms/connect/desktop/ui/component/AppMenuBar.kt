package cn.mercury9.omms.connect.desktop.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import org.jetbrains.skiko.hostOs
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.*
import cn.mercury9.omms.connect.desktop.ui.theme.AppTheme
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import cn.mercury9.utils.compose.string

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