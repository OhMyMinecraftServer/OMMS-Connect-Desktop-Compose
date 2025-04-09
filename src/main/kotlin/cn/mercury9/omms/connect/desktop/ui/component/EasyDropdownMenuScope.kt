package cn.mercury9.omms.connect.desktop.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.arrow_right_24px
import cn.mercury9.omms.connect.desktop.resources.check_24px
import cn.mercury9.utils.compose.painter

@Composable
fun EasyDropdownMenu(
    text: @Composable () -> Unit,
    state: MutableState<Boolean> = remember { mutableStateOf(false) },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    menuOffset: DpOffset = DpOffset(0.dp, 0.dp),
    content: @Composable (EasyDropdownMenuScope.() -> Unit)
) {
    DropdownMenuItem(
        text = text,
        onClick = {
            state.value = !state.value
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .height(32.dp)
    )
    DropdownMenu(
        expanded = state.value,
        onDismissRequest = { state.value = false },
        offset = menuOffset + DpOffset(4.dp, 4.dp),
    ) {
        EasyDropdownMenuScope.PassiveItem(content)
    }
}

object EasyDropdownMenuScope {
    @Composable
    inline fun PassiveItem(content: @Composable EasyDropdownMenuScope.() -> Unit) {
        content(this)
    }

    @Composable
    fun SubMenu(
        text: @Composable () -> Unit,
        state: MutableState<Boolean> = remember { mutableStateOf(false) },
        leadingIcon: @Composable (() -> Unit)? = null,
        submenuOffset: DpOffset = DpOffset(0.dp, 0.dp),
        content: @Composable (EasyDropdownMenuScope.() -> Unit)
    ) {
        DropdownMenuItem(
            text = text,
            onClick = {
                state.value = !state.value
            },
            leadingIcon = leadingIcon,
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Icon(Res.drawable.arrow_right_24px.painter, null)
                    DropdownMenu(
                        expanded = state.value,
                        onDismissRequest = { state.value = false },
                        offset = submenuOffset + DpOffset(36.dp, (-44).dp) + DpOffset(4.dp, 0.dp),
                    ) {
                        PassiveItem(content)
                    }
                }
            },
        )
    }

    @Composable
    fun ButtonItem(
        text: @Composable (() -> Unit),
        enabled: Boolean = true,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        onClick: () -> Unit,
    ) {
        DropdownMenuItem(
            text = text,
            enabled = enabled,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            onClick = onClick
        )
    }

    @Composable
    fun Divider() {
        HorizontalDivider(
            modifier = Modifier
                .height(1.dp)
        )
    }

    @Composable
    fun CheckboxItem(
        text: @Composable (() -> Unit),
        checked: () -> Boolean,
        enabled: Boolean = true,
        leadingIcon: @Composable (() -> Unit)? = null,
        onCheckedChange: (Boolean) -> Unit,
    ) {
        var isChecked by remember { mutableStateOf(checked()) }
        ButtonItem(
            text = text,
            enabled = enabled,
            leadingIcon = leadingIcon,
            trailingIcon = {
                if (isChecked) {
                    Icon(Res.drawable.check_24px.painter, null)
                }
            },
        ) {
            onCheckedChange(!isChecked)
            isChecked = checked()
        }
    }

    @Composable
    fun RadioButtonItem(
        text: @Composable () -> Unit,
        selected: Boolean,
        leadingIcon: @Composable (() -> Unit)? = null,
        enabled: Boolean = true,
        onClick: () -> Unit,
    ) {
        ButtonItem(
            text = text,
            enabled = !selected && enabled,
            leadingIcon = leadingIcon,
            trailingIcon = {
                if (selected) {
                    Icon(Res.drawable.check_24px.painter, null)
                }
            }
        ) {
            onClick()
        }
    }
}
