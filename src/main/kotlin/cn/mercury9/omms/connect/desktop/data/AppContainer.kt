package cn.mercury9.omms.connect.desktop.data

import androidx.navigation.NavController
import cn.mercury9.omms.connect.desktop.data.configs.OmmsServer

object AppContainer {
    var onChangeEnableBackHandler: (enableBackHandler: Boolean) -> Unit = {}
    var onBackKey: () -> Unit = {}

    lateinit var navController: NavController

    var onCurrentOmmsServerChange: MutableMap<String, (OmmsServer?) -> Unit> = mutableMapOf()
    var currentOmmsServer: OmmsServer? = null
        set(value) {
            field = value
            for (func in onCurrentOmmsServerChange.values) {
                func(value)
            }
        }

    private var _enableBackHandler: Boolean = false
    var enableBackHandler: Boolean
        get() {return _enableBackHandler }
        set(value) {
            _enableBackHandler = value
            onChangeEnableBackHandler(value)
        }
}
