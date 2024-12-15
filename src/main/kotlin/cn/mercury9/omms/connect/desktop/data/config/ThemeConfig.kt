package cn.mercury9.omms.connect.desktop.data.config

import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import kotlinx.serialization.Serializable

@Serializable
data class ThemeConfig(
    var themeType: ThemeType = ThemeType.AmazingOrange,
    var contrastType: ContrastType = ContrastType.Default,
    var darkTheme: Boolean = true,
)
