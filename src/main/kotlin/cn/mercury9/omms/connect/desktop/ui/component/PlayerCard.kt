package cn.mercury9.omms.connect.desktop.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.onClick
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCard(
    playerName: String,
    hazeState: HazeState,
    /** 向api要求的头像尺寸，
     *
     * `size=16` 的实际大小为 `8dp`
     * */
    playerHeadSize: Int = 64,
    expandable: Boolean = true,
) {
    var isShowDetails by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = { isShowDetails = true },
        enabled = expandable,
        modifier =
            Modifier
                .fillMaxWidth()
                .height((playerHeadSize / 2 + 32).dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            PlayerHeadImage(
                playerName,
                size = playerHeadSize,
                modifier =
                    Modifier
                        .padding(vertical = 16.dp)
                        .padding(start = 16.dp),
            )
            Text(
                text = playerName,
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(end = 16.dp),
            )
        }
    }

    if (isShowDetails) {
        Popup(
            onDismissRequest = { isShowDetails = false },
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .hazeChild(hazeState)
                    .onClick { isShowDetails = false },
            ) {
                ElevatedCard(
                    modifier =
                        Modifier
                            .widthIn(min = 256.dp)
                            .width(IntrinsicSize.Max)
                            .height((playerHeadSize / 2 + 32).dp)
                            .align(Alignment.Center),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier
                                .fillMaxSize(),
                    ) {
                        PlayerHeadImage(
                            playerName,
                            size = playerHeadSize,
                            modifier =
                                Modifier
                                    .padding(vertical = 16.dp)
                                    .padding(start = 16.dp),
                        )
                        Text(
                            text = playerName,
                            textAlign = TextAlign.End,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(end = 16.dp),
                        )
                    }
                }
            }
        }
    }
}
