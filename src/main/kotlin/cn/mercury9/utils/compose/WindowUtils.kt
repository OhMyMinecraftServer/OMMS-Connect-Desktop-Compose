package cn.mercury9.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowScope
import org.jetbrains.skiko.hostOs
import java.awt.Dimension

@Composable
fun WindowScope.setMinimumSize(
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
) {
    val density = LocalDensity.current
    LaunchedEffect(density) {
        window.minimumSize = with(density) {
            if (hostOs.isWindows) {
                Dimension(width.toPx().toInt(), height.toPx().toInt())
            } else {
                Dimension(width.value.toInt(), height.value.toInt())
            }
        }
    }
}

@Composable
fun WindowScope.setMinimumSize(size: Dp = Dp.Unspecified): Unit =
    setMinimumSize(
        width = size,
        height = size,
    )

@Composable
fun WindowScope.setMinimumSize(size: DpSize = DpSize.Unspecified): Unit =
    setMinimumSize(
        width = size.width,
        height = size.height,
    )