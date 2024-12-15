package cn.mercury9.omms.connect.desktop.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.window.WindowState
import androidx.navigation.NavHostController
import cn.mercury9.omms.connect.desktop.data.configs.AppConfig
import cn.mercury9.omms.connect.desktop.data.configs.Data
import cn.mercury9.omms.connect.desktop.data.configs.OmmsServer
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import icu.takeneko.omms.client.session.ClientSession
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import okio.Path.Companion.toOkioPath
import org.jetbrains.skiko.hostOs
import java.io.File
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

    private val cacheDir =
        if (hostOs.isWindows) {
            File(System.getProperty("user.home"), ".OmmsConnectDesktop/.cache")
        } else if (hostOs.isMacOS) {
            File(System.getProperty("user.home"), "Library/Caches/cn.mercury9.omms.connect.desktop")
        } else if (hostOs.isLinux) {
            File(System.getProperty("user.home"), ".cache/cn.mercury9.omms.connect.desktop")
        } else throw IllegalStateException("Unsupported operating system")

    val config = Data(
        dataDir.resolve("config.json"),
        AppConfig(),
        AppConfig.serializer()
    )

    private val serversMap = mutableStateMapOf<String, OmmsServer>()

    @Suppress("UNCHECKED_CAST")
    private val serversDate = Data(
        dataDir.resolve("servers.json"),
        mutableMapOf(),
        MapSerializer(String.serializer(), OmmsServer.serializer())
                as KSerializer<MutableMap<String, OmmsServer>>
    ).apply {
        serversMap.clear()
        val map = get()
        var flag = false
        map.forEach { (k, v) ->
            if (v.id != k) {
                flag = true
                put(k, v.apply { id = k })
            }
        }
        serversMap.putAll(map)
        if (flag) set(map)
    }

    var servers: MutableMap<String, OmmsServer>
        get() = serversMap
        set(value) {
            serversDate.set(value)
        }

    lateinit var mainWindowState: WindowState

    lateinit var navController: NavHostController

    val imageLoader: ImageLoader = ImageLoader {
        components {
            setupDefaultComponents()
        }
        interceptor {
            // cache 32MB bitmap
            bitmapMemoryCacheConfig {
                maxSize(32 * 1024 * 1024) // 32MB
            }
            // cache 50 image
            imageMemoryCacheConfig {
                maxSize(50)
            }
            // cache 50 painter
            painterMemoryCacheConfig {
                maxSize(50)
            }
            diskCacheConfig {
                directory(cacheDir.toOkioPath().resolve("image_cache"))
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }

    val onChangeCurrentOmmsServer: MutableMap<String, (String?) -> Unit> = mutableMapOf()
    var currentOmmsServerId: String? = null
        set(value) {
            field = value
            for (func in onChangeCurrentOmmsServer.values) {
                func(value)
            }
        }

    val currentOmmsServerSession: ClientSession?
        get() = currentOmmsServerId?.let { sessions[currentOmmsServerId] }

    val onKeyEvent: MutableMap<String, (KeyEvent) -> Boolean> = mutableMapOf()

    val sessions: MutableMap<String, ClientSession> = mutableMapOf()
}
