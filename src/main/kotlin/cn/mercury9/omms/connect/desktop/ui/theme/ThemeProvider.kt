package cn.mercury9.omms.connect.desktop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
enum class ThemeType {
    Default
}

data class AppTheme(
    var darkTheme: Boolean,
    var themeType: ThemeType = ThemeType.Default,
    var contrastType: ContrastType = ContrastType.Default,
)

@Composable
fun ThemeProvider(
    appTheme: AppTheme = AppTheme(darkTheme = isSystemInDarkTheme()),
    content: @Composable () -> Unit
) {
    ThemeProvider(
        themeType = appTheme.themeType,
        contrastType = appTheme.contrastType,
        darkTheme = appTheme.darkTheme,
        content = content,
    )
}

@Composable
fun ThemeProvider(
    themeType: ThemeType = ThemeType.Default,
    contrastType: ContrastType = ContrastType.Default,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val theme = when (themeType) {
        ThemeType.Default -> DefaultTheme
    }
    val colorScheme = theme.colorScheme(darkTheme, contrastType)

    val typography = theme.typography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
