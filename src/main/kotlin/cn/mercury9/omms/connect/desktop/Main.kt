package cn.mercury9.omms.connect.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
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
import org.jetbrains.jewel.window.styling.TitleBarStyle

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1200.dp, 900.dp)
    )
    AppContainer.mainWindowState = windowState

    val isSystemInDarkTheme = isSystemInDarkTheme()

    var configSetupThemeBySystemDarkTheme by AppConfigState.setupThemeBySystemDarkTheme
    var configThemeType by ThemeConfigState.themeType
    var configContrastType by ThemeConfigState.contrastType
    var configDarkTheme by ThemeConfigState.darkTheme

    val appTheme = AppTheme(
        configDarkTheme,
        configThemeType,
        configContrastType,
    )

    if (configSetupThemeBySystemDarkTheme) {
        configDarkTheme = isSystemInDarkTheme
    }

    val textStyle = JewelTheme.createDefaultTextStyle()
    val editorStyle = JewelTheme.createEditorTextStyle()

    val themeDefinition =
        if (configDarkTheme) {
            JewelTheme.darkThemeDefinition(defaultTextStyle = textStyle, editorTextStyle = editorStyle)
        } else {
            JewelTheme.lightThemeDefinition(defaultTextStyle = textStyle, editorTextStyle = editorStyle)
        }

    fun onCloseRequest() {
        for (session in AppContainer.sessions.values) {
            session.close()
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
                    if (configDarkTheme) TitleBarStyle.dark()
                    else TitleBarStyle.light(),
                )
        ) {
            DecoratedWindow(
                onCloseRequest = ::onCloseRequest,
                state = windowState,
                title = stringResource(Res.string.app_name),
                icon = Res.drawable.ic_launcher.painter,
            ) {
                setMinimumSize(916.dp, 687.dp)

                fitScreenEdge(windowState) {
                    windowState.position = it.position
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
