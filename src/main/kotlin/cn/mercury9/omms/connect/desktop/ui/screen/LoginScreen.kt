package cn.mercury9.omms.connect.desktop.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.omms_connect_desktop_compose.generated.resources.Res
import cn.mercury9.omms.connect.desktop.omms_connect_desktop_compose.generated.resources.appName

@Composable
fun LoginScreen() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = Res.string.appName.string,
                modifier = Modifier
            )
        }
    }
}
