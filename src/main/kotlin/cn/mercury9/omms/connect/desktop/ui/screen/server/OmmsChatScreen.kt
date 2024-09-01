package cn.mercury9.omms.connect.desktop.ui.screen.server

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.mercury9.omms.connect.desktop.client.FetchChatHistoryState
import cn.mercury9.omms.connect.desktop.client.fetchChatHistoryFromServer
import cn.mercury9.omms.connect.desktop.client.sendChatMessage
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.arrow_back_24px
import cn.mercury9.omms.connect.desktop.resources.send_24px
import cn.mercury9.omms.connect.desktop.ui.component.PlayerHeadImage
import cn.mercury9.utils.compose.painter
import icu.takeneko.omms.client.data.chatbridge.Broadcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import java.time.Duration

@Composable
fun OmmsChatScreen() {
    var lastFetched by remember { mutableStateOf(AppContainer.currentOmmsServerId) }
    var fetchChatHistoryState: FetchChatHistoryState by remember { mutableStateOf(FetchChatHistoryState.Fetching) }

    if (
        fetchChatHistoryState is FetchChatHistoryState.Fetching
        || lastFetched != AppContainer.currentOmmsServerId
    ) {
        lastFetched = AppContainer.currentOmmsServerId
        CoroutineScope(Dispatchers.IO).launch {
            fetchChatHistoryFromServer(
                AppContainer.sessions[AppContainer.currentOmmsServerId]!!,
            ) {
                fetchChatHistoryState = it
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while (true) {
                delay(Duration.ofSeconds(1))
                fetchChatHistoryFromServer(
                    AppContainer.sessions[AppContainer.currentOmmsServerId]!!,
                ) {
                    if (it is FetchChatHistoryState.Success) {
                        fetchChatHistoryState = it
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        fetchChatHistoryState is FetchChatHistoryState.Success,
        enter = slideIn {
            IntOffset(0, -it.height)
        }
    ) {
        OmmsChatSpace(fetchChatHistoryState as FetchChatHistoryState.Success)
    }
}

@Composable
fun OmmsChatSpace(
    fetchChatHistoryState: FetchChatHistoryState.Success
) {
    val messages = fetchChatHistoryState.messageCache.messages
    var messageToSend by remember { mutableStateOf("") }
    fun sendMessage() {
        val message = messageToSend
        CoroutineScope(Dispatchers.IO).launch {
            sendChatMessage(
                AppContainer.sessions[AppContainer.currentOmmsServerId]!!,
                "GLOBAL",
                message
            )
        }
        messageToSend = ""
    }
    Surface(Modifier
        .fillMaxSize()
        .padding(vertical = 16.dp, horizontal = 64.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            ElevatedCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                OmmsChatHistory(messages)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = messageToSend,
                    onValueChange = { messageToSend = it },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .onKeyEvent {
                            if (it.type == KeyEventType.KeyUp) {
                                if (
                                    it.key == Key.Enter
                                    || it.key == Key.NumPadEnter
                                ) {
                                    sendMessage()
                                    return@onKeyEvent true
                                }
                            }
                            return@onKeyEvent false
                        }
                )
                Button(::sendMessage) {
                    Icon(Res.drawable.send_24px.painter, "send")
                }
            }
        }
    }
}

@Composable
fun OmmsChatHistory(
    history: List<Broadcast>
) {
    val scrollState = rememberLazyListState()
//    var isAutoScrollEnable by remember { mutableStateOf(false) }
    var isAskScrollToBottom by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (scrollState.canScrollForward && !scrollState.isScrollInProgress) {
            scrollState.scrollToItem(history.lastIndex)
        }
    }
//    LaunchedEffect(history) {
//        if (isAutoScrollEnable && scrollState.canScrollForward && !scrollState.isScrollInProgress) {
//            scrollState.animateScrollToItem(history.lastIndex)
//        }
//    }
    LaunchedEffect(isAskScrollToBottom) {
        if (isAskScrollToBottom) {
            println("ask scroll to bottom")
            scrollState.animateScrollToItem(history.lastIndex)
            isAskScrollToBottom = false
        }
    }

    Box(Modifier
        .fillMaxSize()
    ) {
        LazyColumn(
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(history) {
                ElevatedCard {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        if (it.server != "OMMS CENTRAL") {
                            PlayerHeadImage(it.player, 4)
                        }
                        SelectionContainer {
                            Column {
                                Text(
                                    "[${it.server}] ${it.player}",
                                    color = MaterialTheme.colorScheme.outline,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(it.content)
                            }
                        }
                    }
                }
            }
        }

        Box(Modifier.align(Alignment.BottomEnd)) {
            AnimatedVisibility(
                scrollState.canScrollForward,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = {
                        isAskScrollToBottom = true
                    },
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Icon(
                        Res.drawable.arrow_back_24px.painter,
                        null,
                        modifier = Modifier
                            .rotate(-90f)
                    )
                }
            }
        }
    }
}
