package cn.mercury9.omms.connect.desktop.data.config

import kotlinx.serialization.Serializable

@Serializable
enum class OmmsServerListSortBy {
    Id, Name
}

@Serializable
data class AppConfig(
    var setupThemeBySystemDarkTheme: Boolean = true,
    var ommsServerListSortBy: OmmsServerListSortBy = OmmsServerListSortBy.Id
)
