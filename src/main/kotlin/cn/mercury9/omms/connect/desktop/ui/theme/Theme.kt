package cn.mercury9.omms.connect.desktop.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

enum class ContrastType {
    Default, Medium, High
}

interface Theme {
    fun colorScheme(
        darkTheme: Boolean,
        contrastType: ContrastType?
    ): ColorScheme {
        return if (darkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }
    }

    @Composable
    fun typography(): Typography {
        return MaterialTheme.typography
    }
}

object DefaultTheme: Theme
