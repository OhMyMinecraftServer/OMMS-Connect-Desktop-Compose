package cn.mercury9.omms.connect.desktop.ui.window.main.selector.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.cancel
import cn.mercury9.omms.connect.desktop.resources.delete
import cn.mercury9.omms.connect.desktop.resources.hint_confirm_delete
import cn.mercury9.omms.connect.desktop.resources.title_delete_omms_server
import cn.mercury9.utils.compose.string
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun DialogDeleteOmmsServer(
    serverId: String,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(IntrinsicSize.Max)
            ) {
                Text(
                    Res.string.title_delete_omms_server.string,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Text(
                    Res.string.hint_confirm_delete.string(
                        AppContainer.servers[serverId]?.name ?: "Unknown"
                    ).apply {
                        if (Random.nextInt(0..99) == 0) replace("吗", "🐴")
                    },
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .height(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                onDismissRequest()
                            }
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        Text(
                            Res.string.cancel.string,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                    VerticalDivider()
                    Box(
                        modifier = Modifier
                            .clickable {
                                onDismissRequest()
                                AppContainer.servers.apply {
                                    remove(serverId)
                                }.also {
                                    AppContainer.servers = it
                                }
                            }
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        Text(
                            Res.string.delete.string,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}