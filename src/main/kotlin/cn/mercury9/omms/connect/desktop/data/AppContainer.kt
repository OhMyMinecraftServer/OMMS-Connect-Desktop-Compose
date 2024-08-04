package cn.mercury9.omms.connect.desktop.data

import androidx.navigation.NavController

object AppContainer {
    var onChangeEnableBackHandler: (enableBackHandler: Boolean) -> Unit = {}
    var onBackKey: () -> Unit = {}

    lateinit var navController: NavController

    var onChangeCurrentOmmsServer: MutableMap<String, (String?) -> Unit> = mutableMapOf()
    var currentOmmsServerId: String? = null
        set(value) {
            field = value
            for (func in onChangeCurrentOmmsServer.values) {
                func(value)
            }
        }

    var enableBackHandler: Boolean = false
        set(value) {
            field = value
            onChangeEnableBackHandler(value)
        }
}
