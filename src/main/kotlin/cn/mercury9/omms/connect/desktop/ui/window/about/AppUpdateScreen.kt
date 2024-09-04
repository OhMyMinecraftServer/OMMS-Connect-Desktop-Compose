package cn.mercury9.omms.connect.desktop.ui.window.about

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cn.mercury9.omms.connect.desktop.client.http.github.FetchLatestReleaseInfoState
import cn.mercury9.omms.connect.desktop.client.http.github.fetchGithubRepoLatestReleaseInfo
import cn.mercury9.omms.connect.desktop.data.Constants
import cn.mercury9.omms.connect.desktop.resources.*
import cn.mercury9.utils.compose.string

@Composable
fun AppUpdateScreen() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            CurrentVersionInfo()
            LatestReleaseInfo()
        }
    }
}

@Composable
fun CurrentVersionInfo() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                Res.string.title_current_version_info.string,
                style = MaterialTheme.typography.titleLarge,
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        Res.string.label_app_version.string,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Text(Constants.AppInfo.VERSION)
                }
                Column {
                    Text(
                        Res.string.label_core_version.string,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Text(Constants.AppInfo.CORE_VERSION)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LatestReleaseInfo() {
    val uriHandler = LocalUriHandler.current
    var fetchState: FetchLatestReleaseInfoState by remember { mutableStateOf(
        FetchLatestReleaseInfoState.Fetching
    ) }
    LaunchedEffect(fetchState) {
        if (fetchState is FetchLatestReleaseInfoState.Fetching) {
            fetchGithubRepoLatestReleaseInfo(
                Constants.AppInfo.Github.REPO_OWNER,
                Constants.AppInfo.Github.REPO_NAME,
            ) {
                fetchState = it
            }
        }
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    Res.string.title_latest_release.string,
                    style = MaterialTheme.typography.titleLarge,
                )
                val hoverInteraction = remember { MutableInteractionSource() }
                val hoverState = hoverInteraction.collectIsHoveredAsState()
                Text(
                    Res.string.label_releases_url.string,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textDecoration = if (hoverState.value) TextDecoration.Underline else TextDecoration.None,
                    modifier = Modifier
                        .padding(4.dp)
                        .onClick {
                            uriHandler.openUri(
                                Constants.AppInfo.Github.REPO_RELEASES_URL
                            )
                        }
                        .hoverable(hoverInteraction)
                )
            }

            when (fetchState) {
                is FetchLatestReleaseInfoState.Fetching -> {
                    Column {
                        Text(Res.string.label_loading.string)
                        Text(
                            Res.string.label_loading.string,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }

                is FetchLatestReleaseInfoState.Success -> {
                    val data = (fetchState as FetchLatestReleaseInfoState.Success).data
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(data.name)
                            Text(
                                data.tagName,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                        TextButton({
                            uriHandler.openUri(
                                Constants.AppInfo.Github.REPO_LATEST_RELEASE_URL,
                            )
                        }) {
                            Text(Res.string.label_latest_release_url.string)
                        }
                    }
                }

                is FetchLatestReleaseInfoState.Error -> {
                    val error = (fetchState as FetchLatestReleaseInfoState.Error).error
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(3f)
                        ) {
                            Text(Res.string.error_load_fail.string)
                            Text(
                                error,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                        TextButton(
                            onClick = {
                                fetchState = FetchLatestReleaseInfoState.Fetching
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(Res.string.label_refresh.string)
                        }
                    }
                }
            }
        }
    }
}
