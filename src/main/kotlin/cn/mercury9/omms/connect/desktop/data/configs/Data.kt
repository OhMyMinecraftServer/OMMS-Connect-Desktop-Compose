package cn.mercury9.omms.connect.desktop.data.configs

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private val dataDir = Path.of("./data").also {
    if (it.notExists())
        it.createDirectories()
}

private val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    prettyPrint = true
}

val config = Data(
    dataDir.resolve("config.json"),
    AppConfig(),
    AppConfig.serializer()
)
val servers = Data(
    dataDir.resolve("servers.json"),
    listOf(),
    ListSerializer(OmmsServer.serializer())
)

class Data<T : Any>(
    private val filePath: Path,
    private val defaultConfig: T,
    private val serializer: KSerializer<T>
) : ReadOnlyProperty<Data<T>?, T> {

    private var config: T? = null

    fun load(): T {
        return try {
            if (filePath.notExists()) {
                write()
                defaultConfig
            } else {
                config = json.decodeFromString(serializer, filePath.readText())
                config!!
            }
        } catch (e: Exception) {
            write()
            defaultConfig
        }
    }

    fun write() {
        if (config == null) config = defaultConfig
        filePath.deleteIfExists()
        filePath.createFile()
        filePath.writeText(json.encodeToString(serializer, config!!))
    }

    fun get(): T {
        return config ?: load()
    }

    override fun getValue(thisRef: Data<T>?, property: KProperty<*>): T {
        return get()
    }

}