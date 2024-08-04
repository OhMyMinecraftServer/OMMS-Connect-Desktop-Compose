package cn.mercury9.omms.connect.desktop.data.configs

import cn.mercury9.omms.connect.desktop.ui.theme.AppTheme
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import kotlinx.serialization.Serializable

@Serializable
enum class OmmsServerListSortBy {
    Id, Name
}

@Serializable
data class AppConfig(
    var setupThemeBySystemDarkTheme: Boolean = true,
    var theme: AppConfigTheme = AppConfigTheme(),
    var ommsServerListSortBy: OmmsServerListSortBy = OmmsServerListSortBy.Id
)

@Serializable
data class AppConfigTheme(
    var themeType: ThemeType = ThemeType.AmazingOrange,
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


