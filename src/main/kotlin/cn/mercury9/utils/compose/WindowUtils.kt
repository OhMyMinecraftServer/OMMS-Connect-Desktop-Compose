package cn.mercury9.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import org.jetbrains.skiko.hostOs
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

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

@Composable
fun WindowScope.fitScreenEdge(
    windowState: WindowState,
    onWindowStateChanged: (WindowState) -> Unit
) {
    if (hostOs.isWindows) {     // 移动窗口时吸附屏幕边缘
        with(LocalDensity.current) {
            LaunchedEffect(windowState.position) {
                if (windowState.placement != WindowPlacement.Floating) return@LaunchedEffect

                val screenSizePx = Toolkit.getDefaultToolkit().screenSize

                // 屏幕空间被系统占用的部分
                val screenInsetsPx = Toolkit.getDefaultToolkit().getScreenInsets(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration
                )

                val windowAbsorbDistance = 20f

                // position.x = 0 不会贴边，似乎是因为系统加窗口阴影也要算上，除了上边，都有这个问题
                val windowPositionOffsetPx = 7f

                // Left
                if (
                    (windowState.position.x.value
                            + windowPositionOffsetPx.toDp().value
                        ) in (
                            (-windowAbsorbDistance + screenInsetsPx.left.toDp().value)
                                    ..(windowAbsorbDistance + screenInsetsPx.left.toDp().value)
                            )
                ) {
                    val position = windowState.position as WindowPosition.Absolute
                    windowState.position = position.copy(x = -windowPositionOffsetPx.toDp())
                    onWindowStateChanged(windowState)
                }

                // Top
                if (windowState.position.y.value
                    in (
                            (-windowAbsorbDistance + screenInsetsPx.top.toDp().value)
                                    ..(windowAbsorbDistance + screenInsetsPx.top.toDp().value)
                            )
                ) {
                    val position = windowState.position as WindowPosition.Absolute
                    windowState.position = position.copy(y = 0.dp)
                    onWindowStateChanged(windowState)
                }

                // Right
                if ((windowState.position.x.value
                            + windowPositionOffsetPx.toDp().value
                            + windowState.size.width.value
                            ) in (
                            (-windowAbsorbDistance
                                    + screenSizePx.width.toDp().value
                                    - screenInsetsPx.right.toDp().value
                                    )..(windowAbsorbDistance
                                    + screenSizePx.width.toDp().value
                                    + screenInsetsPx.right.toDp().value
                                    )
                            )
                ) {
                    val position = windowState.position as WindowPosition.Absolute
                    windowState.position = position.copy(
                        x = screenSizePx.width.toDp() - windowState.size.width
                                + windowPositionOffsetPx.toDp()
                    )
                    onWindowStateChanged(windowState)
                }

                // Bottom
                if ((windowState.position.y.value
                            + windowPositionOffsetPx.toDp().value
                            + windowState.size.height.value
                            ) in (
                            (-windowAbsorbDistance
                                    + screenSizePx.height.toDp().value
                                    - screenInsetsPx.bottom.toDp().value
                                    )..(windowAbsorbDistance
                                    + screenSizePx.height.toDp().value
                                    + screenInsetsPx.bottom.toDp().value
                                    )
                            )
                ) {
                    val position = windowState.position as WindowPosition.Absolute
                    windowState.position = position.copy(
                        y = screenSizePx.height.toDp() - windowState.size.height
                                + windowPositionOffsetPx.toDp()
                    )
                    onWindowStateChanged(windowState)
                }
            }
        }
    }
}