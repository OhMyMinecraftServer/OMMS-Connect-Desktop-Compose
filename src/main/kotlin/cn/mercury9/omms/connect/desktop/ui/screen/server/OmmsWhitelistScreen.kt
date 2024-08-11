package cn.mercury9.omms.connect.desktop.ui.screen.server

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.mercury9.compose.utils.painter
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.client.FetchWhitelistState
import cn.mercury9.omms.connect.desktop.client.fetchWhitelistFromServer
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.arrow_back_24px
import cn.mercury9.omms.connect.desktop.resources.label_whitelist_player_count
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun OmmsWhitelistScreen() {
    var lastFetched by remember { mutableStateOf(AppContainer.currentOmmsServerId) }
    var fetchWhitelistState: FetchWhitelistState by remember { mutableStateOf(FetchWhitelistState.Fetching) }
    if (
        fetchWhitelistState is FetchWhitelistState.Fetching
        || lastFetched != AppContainer.currentOmmsServerId
    ) {
        lastFetched = AppContainer.currentOmmsServerId
        GlobalScope.launch {
            ensureActive()
            fetchWhitelistFromServer(
                AppContainer.sessions[AppContainer.currentOmmsServerId]
            ) {
                fetchWhitelistState = it
            }
        }
    }
    AnimatedVisibility(
        fetchWhitelistState is FetchWhitelistState.Success,
        enter = slideIn {
            IntOffset(0, -it.height)
        }
    ) {
        OmmsWhitelistList(fetchWhitelistState as FetchWhitelistState.Success)
    }
}

@Composable
fun OmmsWhitelistList(
    fetchWhitelistState: FetchWhitelistState.Success
) {
    var currentShowedWhitelist: String? by remember { mutableStateOf(null) }
    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        AnimatedVisibility(
            currentShowedWhitelist == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyVerticalStaggeredGrid(
                StaggeredGridCells.Adaptive(250.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(Modifier.height(8.dp))
                }
                items(fetchWhitelistState.whitelist.keys.toList()) {
                    OmmsWhitelistItem(
                        it,
                        fetchWhitelistState.whitelist[it]!!.size
                    ) {
                        currentShowedWhitelist = it
                    }
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        AnimatedVisibility(
            visible = currentShowedWhitelist != null,
            enter = slideIn {
                IntOffset(it.width, 0)
            },
            exit = slideOut {
                IntOffset(it.width, 0)
            }
        ) {
            OmmsWhitelistDetail(
                currentShowedWhitelist,
                fetchWhitelistState.whitelist[currentShowedWhitelist] ?: listOf()
            ) {
                currentShowedWhitelist = null
            }
        }
    }
}

@Composable
fun OmmsWhitelistItem(
    name: String,
    number: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(Res.string.label_whitelist_player_count.string(number))
        }
    }
}

@Composable
fun OmmsWhitelistDetail(
    whitelistName: String?,
    whitelist: List<String>,
    onClickButtonBack: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 64.dp)
                .fillMaxSize()
        ) {
            LazyVerticalStaggeredGrid(
                StaggeredGridCells.Adaptive(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(Modifier.height(8.dp))
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    ElevatedCard {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Text(
                                whitelistName.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
                items(whitelist) {
                    ElevatedCard {
                        Text(
                            it,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        )
                    }
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        IconButton(
            onClickButtonBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
        ) {
            Icon(Res.drawable.arrow_back_24px.painter, null)
        }
    }
}
