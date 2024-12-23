package cn.mercury9.omms.connect.desktop.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.WindowState
import androidx.navigation.NavHostController
import cn.mercury9.omms.connect.desktop.data.config.OmmsServer
import cn.mercury9.omms.connect.desktop.data.saver.DataFileSaver
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import icu.takeneko.omms.client.session.ClientSession
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import okio.Path.Companion.toOkioPath
import kotlin.io.path.Path

object AppContainer {

    private val serversMap = mutableStateMapOf<String, OmmsServer>()

    private val serversDate = DataFileSaver(
        Path(Constants.Data.CONFIG_DIR).resolve("servers.json"),
        ::mutableMapOf,
        MapSerializer(String.serializer(), OmmsServer.serializer())
    ).apply {
        val map = get().toMutableMap()
        var flag = false
        map.forEach { (k, v) ->
            if (v.id != k) {
                flag = true
                map[k] = v.apply { id = k }
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
                directory(Path(Constants.Data.CACHE_DIR).resolve("image_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }

    var currentOmmsServerId = mutableStateOf<String?>(null)

    val currentOmmsServerSession: ClientSession?
        get() = sessions[currentOmmsServerId.value]

    val sessions: MutableMap<String, ClientSession> = mutableMapOf()
}
