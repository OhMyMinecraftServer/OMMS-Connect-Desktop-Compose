package cn.mercury9.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

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
