package cn.mercury9.omms.connect.desktop.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.defaultTitleBarStyle
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.TitleBarColors
import org.jetbrains.jewel.window.styling.TitleBarStyle

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