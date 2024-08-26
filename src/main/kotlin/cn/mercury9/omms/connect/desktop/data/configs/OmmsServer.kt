package cn.mercury9.omms.connect.desktop.data.configs

import kotlinx.serialization.Serializable

@Serializable
data class OmmsServer(
    var id: String,
    var name: String,
    var ip: String,
    var port: Int,
    var codeHashed: String? = null,
    var emoji: String? = null,
)
