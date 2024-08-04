package cn.mercury9.omms.connect.desktop.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.mercury9.omms.connect.desktop.data.AppContainer

@Composable
fun MainScreen(

) {
    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        Row {
            OmmsServerSelector(
                modifier = Modifier
                    .sizeIn(maxWidth = 250.dp)
                    .wrapContentWidth(),
            )
            VerticalDivider()
            OmmsServerScreen(
                ommsServer = AppContainer.currentOmmsServer
            )
        }
    }
}
