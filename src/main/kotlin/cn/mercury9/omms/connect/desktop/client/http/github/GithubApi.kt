package cn.mercury9.omms.connect.desktop.client.http.github

import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import cn.mercury9.omms.connect.desktop.client.http.HttpService
import cn.mercury9.omms.connect.desktop.data.github.Release

sealed interface FetchLatestReleaseInfoState {
    data object Fetching : FetchLatestReleaseInfoState
    data class Success(val data: Release) : FetchLatestReleaseInfoState
    data class Error(val error: String) : FetchLatestReleaseInfoState
}

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchGithubRepoLatestReleaseInfo(
    owner: String,
    repo: String,
    stateListener: (FetchLatestReleaseInfoState) -> Unit
) {
    try {
        val response = withContext(Dispatchers.IO) {
            HttpService.get("https://api.github.com/repos/$owner/$repo/releases/latest")
        }
        when (response.status.value) {
            in 200..299 -> {
                stateListener(
                    FetchLatestReleaseInfoState.Success(
                        json.decodeFromString(Release.serializer(), response.body())
                    )
                )
            }

            else -> {
                stateListener(FetchLatestReleaseInfoState.Error(response.body()))
            }
        }
    } catch (e: Exception) {
        stateListener(FetchLatestReleaseInfoState.Error(e.localizedMessage))
    }
}
