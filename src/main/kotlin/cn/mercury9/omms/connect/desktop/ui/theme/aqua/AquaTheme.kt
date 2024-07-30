package cn.mercury9.omms.connect.desktop.ui.theme.aqua

import androidx.compose.material3.ColorScheme
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.Theme


object AquaTheme: Theme {
    override fun colorScheme(darkTheme: Boolean, contrastType: ContrastType?): ColorScheme {
        return when (contrastType) {
            ContrastType.High -> when {
                darkTheme ->
                    AquaThemeColors.highContrastDarkColorScheme
                else ->
                    AquaThemeColors.highContrastLightColorScheme
            }
            ContrastType.Medium -> when {
                darkTheme ->
                    AquaThemeColors.mediumContrastDarkColorScheme
                else ->
                    AquaThemeColors.mediumContrastLightColorScheme
            }
            else -> when {
                darkTheme ->
                    AquaThemeColors.darkScheme
                else ->
                    AquaThemeColors.lightScheme
            }
        }
    }
}
