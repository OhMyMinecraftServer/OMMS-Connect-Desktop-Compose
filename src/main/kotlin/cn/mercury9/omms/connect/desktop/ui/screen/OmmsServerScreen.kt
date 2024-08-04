package cn.mercury9.omms.connect.desktop.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.mercury9.compose.utils.painter
import cn.mercury9.compose.utils.string
import cn.mercury9.omms.connect.desktop.data.configs.servers
import cn.mercury9.omms.connect.desktop.resources.Res
import cn.mercury9.omms.connect.desktop.resources.app_name
import cn.mercury9.omms.connect.desktop.resources.error_unknown_error
import cn.mercury9.omms.connect.desktop.resources.hint_choose_omms_server
import cn.mercury9.omms.connect.desktop.resources.ic_launcher
import cn.mercury9.omms.connect.desktop.resources.success
import cn.mercury9.omms.connect.desktop.resources.welcome
import cn.mercury9.omms.connect.desktop.resources.working

sealed interface OmmsServerLoadingState {
    data object NoServer : OmmsServerLoadingState
    data class Loading(val serverId: String) : OmmsServerLoadingState
    data class Error(val msg: String? = null) : OmmsServerLoadingState
    data object Success : OmmsServerLoadingState
}

@Composable
fun OmmsServerScreen(
    ommsServerId: String? = null,
) {
    var state: OmmsServerLoadingState by remember { mutableStateOf(OmmsServerLoadingState.NoServer) }
    ommsServerId?.let {
        state = OmmsServerLoadingState.Loading(it)
    }
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (state !is OmmsServerLoadingState.Success) {
            Welcome(state)
        } else {
            Text("TODO")
        }
    }
}

@Composable
fun Welcome(
    state: OmmsServerLoadingState
) {
    Box(
        Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Center)
                .shadow(
                    elevation = 64.dp,
                    shape = CircleShape,
                    ambientColor =  MaterialTheme.colorScheme.primary,
                    spotColor = MaterialTheme.colorScheme.primary
                )
                .padding(16.dp)
        ) {
            Image(Res.drawable.ic_launcher.painter, "logo")
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    Res.string.welcome.string,
                    color = MaterialTheme.colorScheme.outline,
                    fontStyle = FontStyle.Italic,
                )
                Text(
                    Res.string.app_name.string,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Box(
                    modifier = Modifier
                        .size(300.dp, 14.dp)
                ) {
                    Text(
                        text = when (state) {
                            OmmsServerLoadingState.NoServer ->
                                Res.string.hint_choose_omms_server.string

                            is OmmsServerLoadingState.Loading ->
                                Res.string.working.string(servers.get()[state.serverId]?.name.toString())

                            is OmmsServerLoadingState.Error ->
                                Res.string.error_unknown_error.string

                            is OmmsServerLoadingState.Success ->
                                Res.string.success.string
                        },
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.labelSmall,
                        overflow = TextOverflow.Visible,
                    )
                }
            }
        }
    }
}
