package cn.mercury9.omms.connect.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.mercury9.compose.utils.painter
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.app_name
import cn.mercury9.omms.connect.desktop.resources.ic_launcher
import cn.mercury9.omms.connect.desktop.ui.screen.MainScreen
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeProvider
import org.jetbrains.compose.resources.stringResource

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
        appTheme = AppContainer.config.get().theme.appTheme().apply {
            if (AppContainer.config.get().setupThemeBySystemDarkTheme) {
                darkTheme = isSystemInDarkTheme
            }
        }
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
        }
    ) {
        ThemeProvider(appTheme) {
            MainScreen()
        }
    }
}
