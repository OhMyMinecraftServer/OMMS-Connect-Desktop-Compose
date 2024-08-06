package cn.mercury9.omms.connect.desktop.data

import androidx.compose.ui.input.key.KeyEvent
import cn.mercury9.omms.connect.desktop.data.configs.AppConfig
import cn.mercury9.omms.connect.desktop.data.configs.Data
import cn.mercury9.omms.connect.desktop.data.configs.OmmsServer
import icu.takeneko.omms.client.session.ClientSession
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import org.jetbrains.skiko.hostOs
import javax.swing.UIManager.put
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

object AppContainer {

    private val dataDir = Path(
        System.getProperty("user.home") + if (hostOs.isMacOS) {
            "/.config/cn.mercury9.omms.connect.desktop"
        } else {
            "/.OmmsConnectDesktop"
        }
    ).also {
        it.createDirectories()
    }

    val config = Data(
        dataDir.resolve("config.json"),
        AppConfig(),
        AppConfig.serializer()
    )

    @Suppress("UNCHECKED_CAST")
    val servers = Data(
        dataDir.resolve("servers.json"),
        mutableMapOf(),
        MapSerializer(String.serializer(), OmmsServer.serializer())
                as KSerializer<MutableMap<String, OmmsServer>>
    ).apply {
        val map = get()
        var flag = false
        map.forEach { (k, v) ->
            if (v.id != k) {
                flag = true
                put(k, v.apply { id = k })
            }
        }
        if (flag) set(map)
    }

    val onChangeCurrentOmmsServer: MutableMap<String, (String?) -> Unit> = mutableMapOf()
    var currentOmmsServerId: String? = null
        set(value) {
            field = value
            for (func in onChangeCurrentOmmsServer.values) {
                func(value)
            }
        }

    val onKeyEvent: MutableMap<String, (KeyEvent) -> Boolean> = mutableMapOf()

    val sessions: MutableMap<String, ClientSession> = mutableMapOf()
}
