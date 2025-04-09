package cn.mercury9.omms.connect.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.data.config.configState.AppConfigState
import cn.mercury9.omms.connect.desktop.data.config.configState.ThemeConfigState
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.app_name
import cn.mercury9.omms.connect.desktop.resources.ic_launcher
import cn.mercury9.omms.connect.desktop.resources.title_check_update
import cn.mercury9.omms.connect.desktop.ui.component.AppMenuBar
import cn.mercury9.omms.connect.desktop.ui.component.AppTitleBar
import cn.mercury9.omms.connect.desktop.ui.theme.AppTheme
import cn.mercury9.omms.connect.desktop.ui.theme.DarkThemeDetector
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeProvider
import cn.mercury9.omms.connect.desktop.ui.window.about.AppUpdateScreen
import cn.mercury9.omms.connect.desktop.ui.window.main.MainScreen
import cn.mercury9.utils.compose.fitScreenEdge
import cn.mercury9.utils.compose.painter
import cn.mercury9.utils.compose.setMinimumSize
import cn.mercury9.utils.compose.string
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow

fun main() = application {
    val mainWindowState = rememberWindowState(
        size = DpSize(1200.dp, 900.dp)
    )
    AppContainer.mainWindowState = mainWindowState

    var isSystemInDarkTheme by remember { mutableStateOf(DarkThemeDetector.isDarkTheme) }

    DarkThemeDetector.registerListener {
        isSystemInDarkTheme = it
    }

    val configFollowSystemDarkTheme by AppConfigState.followSystemDarkTheme
    val configThemeType by ThemeConfigState.themeType
    val configContrastType by ThemeConfigState.contrastType
    var configDarkTheme by ThemeConfigState.darkTheme

    LaunchedEffect(configFollowSystemDarkTheme, isSystemInDarkTheme) {
        if (configFollowSystemDarkTheme) {
            configDarkTheme = isSystemInDarkTheme
            ThemeConfigState.saveToConfigFile()
        }
    }

    val intUiThemeDefinition =
        if (configDarkTheme) {
            JewelTheme.darkThemeDefinition()
        } else {
            JewelTheme.lightThemeDefinition()
        }

    fun onCloseRequest() {
        for (session in AppContainer.sessions.values) {
            session.close()
        }
        exitApplication()
    }

    var showDialogWindowAppUpdate by remember { mutableStateOf(false) }

    val appTheme = AppTheme(
        configDarkTheme,
        configThemeType,
        configContrastType,
    )

    ThemeProvider(appTheme) {
        IntUiTheme(
            theme = intUiThemeDefinition,
            styling = ComponentStyling.default()
                .decoratedWindow()
        ) {
            DecoratedWindow(
                onCloseRequest = ::onCloseRequest,
                state = mainWindowState,
                title = stringResource(Res.string.app_name),
                icon = Res.drawable.ic_launcher.painter,
            ) {
                setMinimumSize(916.dp, 687.dp)

                fitScreenEdge(mainWindowState) {
                    mainWindowState.position = it.position
                }

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
                        size = DpSize(444.dp, 333.dp)
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
