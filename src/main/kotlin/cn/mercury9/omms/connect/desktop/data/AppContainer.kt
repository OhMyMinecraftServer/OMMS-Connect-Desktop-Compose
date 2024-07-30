package cn.mercury9.omms.connect.desktop.data

import androidx.navigation.NavController

object AppContainer {
    var onChangeEnableBackHandler: (enableBackHandler: Boolean) -> Unit = {}
    var onBackKey: () -> Unit = {}

    var navController: NavController? = null

    private var _enableBackHandler: Boolean = false
    var enableBackHandler: Boolean
        get() {return _enableBackHandler }
        set(value) {
            _enableBackHandler = value
            onChangeEnableBackHandler(value)
        }
}
