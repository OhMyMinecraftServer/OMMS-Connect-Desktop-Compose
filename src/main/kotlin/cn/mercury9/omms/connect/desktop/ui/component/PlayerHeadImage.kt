package cn.mercury9.omms.connect.desktop.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cn.mercury9.omms.connect.desktop.data.AppContainer
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.ui.AutoSizeImage

@Composable
fun PlayerHeadImage(
    playerName: String,
    scale: Int = 1,
    size: Int = 16 * scale,
    imageLoader: ImageLoader = AppContainer.imageLoader,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalImageLoader provides remember { imageLoader },
    ) {
        AutoSizeImage(
            url = "https://minotar.net/helm/$playerName/$size",
            contentDescription = playerName,
            modifier = modifier,
        )
    }
}
