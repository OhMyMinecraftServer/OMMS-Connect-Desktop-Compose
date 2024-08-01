package cn.mercury9.omms.connect.desktop.data.configs

import kotlinx.serialization.Serializable

@Serializable
data class OmmsServer(
    val id: String,
    var name: String,
    var ip: String,
    var port: Int,
    var code: Int? = null,
    var emoji: String? = null,
)