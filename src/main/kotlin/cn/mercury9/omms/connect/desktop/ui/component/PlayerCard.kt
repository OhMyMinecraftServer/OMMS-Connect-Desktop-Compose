package cn.mercury9.omms.connect.desktop.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PlayerCard(
    playerName: String,
    /** 向api要求的头像尺寸，
     *
     * `size=16` 的实际大小为 `8dp`
     * */
    playerHeadSize: Int = 64,
    expandable: Boolean = true,
    onClick: (playerName: String) -> Unit = {}
) {
    ElevatedCard(
        onClick = { onClick(playerName) },
        enabled = expandable,
        modifier =
            Modifier
                .wrapContentWidth()
                .height((playerHeadSize / 2 + 32).dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
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
                        .wrapContentWidth()
                        .padding(end = 16.dp),
            )
        }
    }
}
