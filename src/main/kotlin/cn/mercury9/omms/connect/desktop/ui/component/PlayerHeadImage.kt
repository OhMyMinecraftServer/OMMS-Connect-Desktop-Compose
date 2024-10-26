package cn.mercury9.omms.connect.desktop.ui.component

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.ui.AutoSizeImage
import cn.mercury9.omms.connect.desktop.data.AppContainer

@Composable
fun PlayerHeadImage(
    playerName: String,

    /** 向api要求的头像尺寸，会被`size`覆盖
     *
     * `scale=1` == `size=16`
     *
     * `size=16` 的实际大小为 `8dp`
     * */
    scale: Int = 1,

    /** 向api要求的头像尺寸，
     *
     * `size=16` 的实际大小为 `8dp`
     * */
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
            modifier = modifier
                .sizeIn(minWidth = size.dp / 2, maxHeight = size.dp / 2),
        )
    }
}
