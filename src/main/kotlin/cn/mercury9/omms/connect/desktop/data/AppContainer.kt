package cn.mercury9.omms.connect.desktop.data

import androidx.compose.ui.input.key.KeyEvent
import androidx.navigation.NavController
import icu.takeneko.omms.client.session.ClientSession

object AppContainer {

    lateinit var navController: NavController

    var onChangeCurrentOmmsServer: MutableMap<String, (String?) -> Unit> = mutableMapOf()
    var currentOmmsServerId: String? = null
        set(value) {
            field = value
            for (func in onChangeCurrentOmmsServer.values) {
                func(value)
            }
        }

    var onKeyEvent: MutableMap<String, (KeyEvent) -> Boolean> = mutableMapOf()

    var sessions: MutableMap<String, ClientSession> = mutableMapOf()
}
