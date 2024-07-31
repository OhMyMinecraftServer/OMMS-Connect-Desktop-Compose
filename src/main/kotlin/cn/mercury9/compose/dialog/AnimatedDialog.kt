package cn.mercury9.compose.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun AnimatedDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
    ) {
        Dialog(
            onDismissRequest = onDismissRequest,
            content = content
        )
    }
}
