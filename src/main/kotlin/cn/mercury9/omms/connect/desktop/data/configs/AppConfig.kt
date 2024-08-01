package cn.mercury9.omms.connect.desktop.data.configs

import cn.mercury9.omms.connect.desktop.ui.theme.AppTheme
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    var setupThemeBySystemDarkTheme: Boolean = true,
    var theme: AppConfigTheme = AppConfigTheme(),
)

@Serializable
data class AppConfigTheme(
    var themeType: ThemeType = ThemeType.Default,
    var contrastType: ContrastType = ContrastType.Default,
    var darkTheme: Boolean = true,
) {
    fun appTheme(): AppTheme {
        return AppTheme(
            themeType = themeType,
            contrastType = contrastType,
            darkTheme = darkTheme
        )
    }
}


