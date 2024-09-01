package cn.mercury9.omms.connect.desktop.ui.screen.server

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cn.mercury9.omms.connect.desktop.client.FetchWhitelistState
import cn.mercury9.omms.connect.desktop.client.addPlayerToWhitelist
import cn.mercury9.omms.connect.desktop.client.fetchWhitelistFromServer
import cn.mercury9.omms.connect.desktop.client.removePlayerFromWhitelist
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.*
import cn.mercury9.omms.connect.desktop.ui.component.PlayerHeadImage
import cn.mercury9.utils.compose.CardColorSets
import cn.mercury9.utils.compose.painter
import cn.mercury9.utils.compose.string

@Composable
fun OmmsWhitelistScreen() {
    var lastFetched by remember { mutableStateOf(AppContainer.currentOmmsServerId) }
    var fetchWhitelistState: FetchWhitelistState by remember { mutableStateOf(FetchWhitelistState.Fetching) }
    if (
        fetchWhitelistState is FetchWhitelistState.Fetching
        || lastFetched != AppContainer.currentOmmsServerId
    ) {
        lastFetched = AppContainer.currentOmmsServerId
        CoroutineScope(Dispatchers.IO).launch {
            fetchWhitelistFromServer(
                AppContainer.sessions[AppContainer.currentOmmsServerId]!!
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

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun OmmsWhitelistDetail(
    whitelistName: String?,
    whitelist: List<String>,
    onClickButtonBack: () -> Unit
) {
    var showDialogAddPlayer by remember { mutableStateOf(false) }
    var showDialogRemovePlayer by remember { mutableStateOf(false) }
    var playerName by remember { mutableStateOf("") }

    val hazeState = remember { HazeState() }

    Box(Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 64.dp)
                .fillMaxSize()
        ) {
            Box {
                LazyVerticalStaggeredGrid(
                    StaggeredGridCells.Adaptive(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,
                    contentPadding = PaddingValues(top = 90.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxSize()
                        .haze(hazeState, HazeMaterials.ultraThin())
                ) {
                    items(whitelist) {
                        ElevatedCard(
                            onClick = {
                                showDialogRemovePlayer = true
                                playerName = it
                            },
                            modifier = Modifier
                                .heightIn(min = 64.dp)
                        ) {
                            Box(
                                Modifier.fillMaxSize(),
                            ) {
                                PlayerHeadImage(
                                    it,
                                    size = 64,
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(16.dp)
                                )
                                Text(
                                    it,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .align(Alignment.CenterEnd)
                                )
                            }
                        }
                    }
                }
                Card(
                    colors = CardColorSets.Transparent,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp)
                        .padding(top = 16.dp)
                        .hazeChild(
                            state = hazeState,
                            shape = CardDefaults.shape,
                            style = HazeMaterials.ultraThin()
                        )
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(0.1f),
                    ) {
                        Text(
                            whitelistName ?: "null",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .background(Color.Transparent)
                                .padding(16.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                showDialogAddPlayer = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Res.drawable.add_24px.painter, null)
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
    if (showDialogAddPlayer) {
        DialogAddPlayerToWhitelist(whitelistName!!) {
            showDialogAddPlayer = false
        }
    }
    if (showDialogRemovePlayer) {
        DialogConfirmRemovePlayer(
            whitelistName!!,
            playerName
        ) {
            showDialogRemovePlayer = false
        }
    }
}

@Composable
fun DialogAddPlayerToWhitelist(
    whitelist: String,
    onDismiss: () -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismiss
    ) {
        ElevatedCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .padding(16.dp)
            ) {
                Text(
                    Res.string.title_add_player_to_whitelist.string,
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    label = { Text(Res.string.player_name.string) },
                    value = playerName,
                    onValueChange = {
                        playerName = it.filter { char ->
                            char.isLetterOrDigit() || char == '_'
                        }
                    },
                    singleLine = true,
                    enabled = !loading,
                    isError = playerName.isBlank(),
                    supportingText = {
                        if (playerName.isBlank()) {
                            Text(Res.string.error_blank.string)
                        }
                    }
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = onDismiss,
                        enabled = !loading,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(Res.string.cancel.string)
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            loading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                addPlayerToWhitelist(
                                    AppContainer.sessions[AppContainer.currentOmmsServerId]!!,
                                    whitelist,
                                    playerName
                                ) {}
                            }
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(Res.string.confirm.string)
                    }
                }
                Text(Res.string.hint_result_may_need_refresh.string)
            }
        }
    }
}

@Composable
fun DialogConfirmRemovePlayer(
    whitelist: String,
    playerName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button({
                CoroutineScope(Dispatchers.IO).launch {
                    removePlayerFromWhitelist(
                        AppContainer.sessions[AppContainer.currentOmmsServerId]!!,
                        whitelist,
                        playerName
                    ) {
                    }
                }
                onDismiss()
            }) {
                Text(Res.string.confirm.string)
            }
        },
        dismissButton = {
            Button(onDismiss) {
                Text(Res.string.cancel.string)
            }
        },
        title = {
            Text(
                Res.string.title_remove_player_from_whitelist.string(playerName, whitelist)
                    + "\n"
                    + Res.string.hint_result_may_need_refresh.string)
        }
    )
}
