package cn.mercury9.omms.connect.desktop.ui.screen.server

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.mercury9.omms.connect.desktop.client.FetchAnnouncementState
import cn.mercury9.omms.connect.desktop.client.fetchAnnouncementFromServer
import cn.mercury9.omms.connect.desktop.data.AppContainer
import icu.takeneko.omms.client.data.announcement.Announcement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.format.char

@Composable
fun OmmsAnnouncementScreen() {
    var lastFetched by remember { mutableStateOf(AppContainer.currentOmmsServerId) }
    var fetchAnnouncementState: FetchAnnouncementState by remember { mutableStateOf(FetchAnnouncementState.Fetching) }

    if (
        fetchAnnouncementState is FetchAnnouncementState.Fetching
        || lastFetched != AppContainer.currentOmmsServerId
    ) {
        lastFetched = AppContainer.currentOmmsServerId
        CoroutineScope(Dispatchers.IO).launch {
            fetchAnnouncementFromServer(
                AppContainer.currentOmmsServerSession!!
            ) {
                fetchAnnouncementState = it
            }
        }
    }

    AnimatedVisibility(
        visible = fetchAnnouncementState is FetchAnnouncementState.Success,
        enter = slideIn {
            IntOffset(0, -it.height)
        },
    ) {
        OmmsAnnouncementList((fetchAnnouncementState as FetchAnnouncementState.Success).data)
    }
}

@Composable
fun OmmsAnnouncementList(
    announcements: Map<String, Announcement>
) {
    Surface(Modifier
        .fillMaxSize()
        .padding(vertical = 16.dp, horizontal = 64.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                announcements.values.toList().sortedBy { it.timeMillis }.reversed(),
                key = { it.id }
            ) {
                SelectionContainer {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                it.title,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                Instant.fromEpochMilliseconds(it.timeMillis)
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .format(LocalDateTime.Format {
                                        date(LocalDate.Formats.ISO)
                                        char(' ')
                                        hour(); char(':'); minute(); char(':'); second()
                                    }),
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            HorizontalDivider()
                            Column(Modifier
                                .padding(top = 16.dp)
                            ) {
                                for (item in it.content) {
                                    Text(item)
                                }
                            }
                        }
                    }
                }
            }

        }

    }
}
