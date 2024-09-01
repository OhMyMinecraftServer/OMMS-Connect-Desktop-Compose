package cn.mercury9.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.*

val DrawableResource.painter
    @Composable
    get() = painterResource(this)

val StringResource.string
    @Composable
    get() = stringResource(this)

@Composable
fun StringResource.string(vararg formatArgs: Any): String {
    return stringResource(this, *formatArgs)
}

val StringArrayResource.stringArray
    @Composable
    get() = stringArrayResource(this)

@Composable
fun PluralStringResource.plural(quantity: Int) =
    pluralStringResource(this, quantity)

@Composable
fun FontResource.font(weight: FontWeight, style: FontStyle) =
    Font(this, weight, style)
