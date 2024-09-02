package cn.mercury9.omms.connect.desktop.data.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
    val name: String,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("published_at")
    val publishedAt: String,
    val assets: List<ReleaseAsset>
)

@Serializable
data class ReleaseAsset(
    val name: String,
    @SerialName("browser_download_url")
    val browserDownloadUrl: String,
)
