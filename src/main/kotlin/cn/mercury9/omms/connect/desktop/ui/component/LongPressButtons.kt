package cn.mercury9.omms.connect.desktop.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun LongPressIconButton(
    responseTime: Duration = 1.seconds,
    enabled: Boolean = true,
    progressColor: Color = ProgressIndicatorDefaults.circularColor,
    onClick: () -> Unit,
    onProgressChange: (Float) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var pressedTime by remember { mutableStateOf(0.milliseconds) }
    var lastPressedInstant by remember { mutableStateOf(null as Instant?) }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(pressedTime) {
        progress = (pressedTime / responseTime).toFloat()
        onProgressChange(progress)
        if (pressedTime > responseTime) {
            onClick()
        }
    }

    if (isPressed) {
        val now = Clock.System.now()
        if (pressedTime < responseTime) {
            lastPressedInstant?.let {
                val delta = now - it
                pressedTime += delta
            }
        } else {
            onClick()
        }
        lastPressedInstant = now

    } else if (pressedTime < 0.milliseconds) {
        pressedTime = 0.milliseconds
        lastPressedInstant = null
    } else if (pressedTime > 0.milliseconds) {
        val now = Clock.System.now()
        lastPressedInstant?.let {
            val delta = now - it
            pressedTime -= delta * 2
        }
        lastPressedInstant = now
    }

    Box {
        IconButton(
            enabled = enabled,
            onClick = {},
            interactionSource = interactionSource,
            content = content,
            modifier = Modifier
                .align(Alignment.Center)
        )
        CircularProgressIndicator(
            color = progressColor,
            trackColor = Color.Transparent,
            progress = { progress },
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}
