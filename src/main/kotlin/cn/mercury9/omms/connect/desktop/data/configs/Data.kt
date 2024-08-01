package cn.mercury9.omms.connect.desktop.data.configs

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

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
    mutableListOf(),
    ListSerializer(OmmsServer.serializer()) as KSerializer<MutableList<OmmsServer>>
)

class Data<T : Any>(
    private val filePath: Path,
    private val defaultConfig: T,
    private val serializer: KSerializer<T>
) {

    private var config: T? = null

    var onConfigChange: MutableMap<String, (T) -> Unit> = mutableMapOf()
    private fun callOnConfigChange(value: T) {
        for (func in onConfigChange.values) {
            func(value)
        }
    }

    fun load(): T {
        return try {
            if (filePath.notExists()) {
                defaultConfig.also {
                    config = it
                    write(it)
                }
            } else {
                json.decodeFromString(serializer, filePath.readText()).also {
                    config = it
                }
            }
        } catch (e: Exception) {
            defaultConfig.also {
                config = it
                write(it)
            }
        }
    }

    fun write(config: T?) {
        filePath.deleteIfExists()
        filePath.createFile()
        filePath.writeText(json.encodeToString(serializer, config?:defaultConfig))
    }
    fun write() {
        write(config)
    }

    fun get(): T {
        return config ?: load()
    }
    fun set(value: T) {
        config = value
        write(value)
        callOnConfigChange(value)
    }
}