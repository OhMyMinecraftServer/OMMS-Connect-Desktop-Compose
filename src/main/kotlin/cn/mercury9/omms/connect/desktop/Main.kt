package cn.mercury9.omms.connect.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.mercury9.compose.utils.painter
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.data.configs.appConfig
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.app_name
import cn.mercury9.omms.connect.desktop.resources.ic_launcher
import cn.mercury9.omms.connect.desktop.ui.screen.MainScreen
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeProvider
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    val windowState = rememberWindowState()

    val isSystemInDarkTheme = isSystemInDarkTheme()
    var appTheme by remember { mutableStateOf(
        appConfig.configTheme(
            darkTheme = if (appConfig.configData.setupThemeBySystemDarkTheme) {
                isSystemInDarkTheme
            } else {
                appConfig.configTheme.darkTheme
            }
        )
    ) }

    appConfig.setAppTheme(appTheme)
    appConfig.onSetConfigTheme = { appTheme = it }

    var enableBackHandler by remember { mutableStateOf(
        AppContainer.enableBackHandler
    ) }
    AppContainer.onChangeEnableBackHandler = {
        enableBackHandler = it
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = stringResource(Res.string.app_name),
        icon = Res.drawable.ic_launcher.painter,
        onKeyEvent = {
            if (it.key == Key.Escape) {
                if (enableBackHandler) {
                    AppContainer.onBackKey()
                    return@Window true
                }
            }
            return@Window false
        }
    ) {
        ThemeProvider(appTheme) {
            MainScreen()
        }
    }
}
