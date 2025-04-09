package cn.mercury9.omms.connect.desktop.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import cn.mercury9.omms.connect.desktop.data.config.configState.AppConfigState
import cn.mercury9.omms.connect.desktop.data.config.configState.ThemeConfigState
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.option_follow_system_dark_theme
import cn.mercury9.omms.connect.desktop.resources.option_theme_dark
import cn.mercury9.omms.connect.desktop.resources.option_theme_light
import cn.mercury9.omms.connect.desktop.resources.title_check_update
import cn.mercury9.omms.connect.desktop.resources.title_settings
import cn.mercury9.omms.connect.desktop.resources.title_settings_contrast
import cn.mercury9.omms.connect.desktop.resources.title_settings_dark
import cn.mercury9.omms.connect.desktop.resources.title_settings_theme
import cn.mercury9.omms.connect.desktop.ui.theme.AppTheme
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import cn.mercury9.utils.compose.string
import org.jetbrains.skiko.hostOs

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
    var configFollowSystemDarkTheme by AppConfigState.followSystemDarkTheme
    var configThemeType by ThemeConfigState.themeType
    var configContrastType by ThemeConfigState.contrastType
    var configDarkTheme by ThemeConfigState.darkTheme

    MenuBar {
        Menu(Res.string.title_settings.string) {
            Menu(Res.string.title_settings_theme.string) {
                Menu(Res.string.title_settings_theme.string) {
                    for (themeType in ThemeType.entries) {
                        RadioButtonItem(
                            themeType.name,
                            themeType == appTheme.themeType
                        ) {
                            configThemeType = themeType
                            ThemeConfigState.saveToConfigFile()
                        }
                    }
                }
                Menu(Res.string.title_settings_dark.string) {
                    CheckboxItem(
                        Res.string.option_follow_system_dark_theme.string,
                        configFollowSystemDarkTheme
                    ) {
                        configFollowSystemDarkTheme = it
                        AppConfigState.saveToConfigFile()
                    }
                    Separator()
                    RadioButtonItem(
                        Res.string.option_theme_light.string,
                        !appTheme.darkTheme,
                        enabled = !configFollowSystemDarkTheme
                    ) {
                        configDarkTheme = false
                        ThemeConfigState.saveToConfigFile()
                    }
                    RadioButtonItem(
                        Res.string.option_theme_dark.string,
                        appTheme.darkTheme,
                        enabled = !configFollowSystemDarkTheme
                    ) {
                        configDarkTheme = true
                        ThemeConfigState.saveToConfigFile()
                    }
                }
                Menu(Res.string.title_settings_contrast.string) {
                    for (contrastType in ContrastType.entries) {
                        RadioButtonItem(
                            contrastType.name,
                            contrastType == appTheme.contrastType
                        ) {
                            configContrastType = contrastType
                            ThemeConfigState.saveToConfigFile()
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
    var configFollowSystemDarkTheme by AppConfigState.followSystemDarkTheme
    var configThemeType by ThemeConfigState.themeType
    var configContrastType by ThemeConfigState.contrastType
    var configDarkTheme by ThemeConfigState.darkTheme

    Row {
        EasyDropdownMenu({
            Text(
                Res.string.title_settings.string,
                style = MaterialTheme.typography.labelLarge
            )
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
                            configThemeType = themeType
                            ThemeConfigState.saveToConfigFile()
                        }
                    }
                }
                SubMenu({
                    Text(Res.string.title_settings_dark.string)
                }) {
                    CheckboxItem(
                        text = { Text(Res.string.option_follow_system_dark_theme.string) },
                        checked = {
                            configFollowSystemDarkTheme
                        },
                    ) {
                        configFollowSystemDarkTheme = it
                        AppConfigState.saveToConfigFile()
                    }
                    Divider()
                    RadioButtonItem(
                        text = { Text(Res.string.option_theme_light.string) },
                        selected = !appTheme.darkTheme,
                        enabled = !configFollowSystemDarkTheme
                    ) {
                        configDarkTheme = false
                        ThemeConfigState.saveToConfigFile()
                    }
                    RadioButtonItem(
                        text = { Text(Res.string.option_theme_dark.string) },
                        selected = appTheme.darkTheme,
                        enabled = !configFollowSystemDarkTheme
                    ) {
                        configDarkTheme = true
                        ThemeConfigState.saveToConfigFile()
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
                            configContrastType = contrastType
                            ThemeConfigState.saveToConfigFile()
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