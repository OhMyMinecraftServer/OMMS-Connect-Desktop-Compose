package cn.mercury9.omms.connect.desktop.data.saver

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class DataFileSaver<T>(
    private val filePath: Path,
    private val defaultConfig: () -> T,
    private val serializer: KSerializer<T>,
    private val stringFormat: StringFormat = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    },
): DataSaver<T> {

    private var config: T = load()

    override fun load(): T {
        return try {
            if (filePath.notExists()) {
                defaultConfig().also {
                    config = it
                    save(it)
                }
            } else {
                stringFormat.decodeFromString(serializer, filePath.readText()).also {
                    config = it
                }
            }
        } catch (e: Exception) {
            defaultConfig().also {
                config = it
                save(it)
            }
        }
    }

    private fun save(config: T?): DataFileSaver<T> {
        filePath.deleteIfExists()
        filePath.createFile()
        filePath.writeText(stringFormat.encodeToString(serializer, config?:defaultConfig()))
        println("write data to $filePath:\n\t $config")
        return this
    }

    override fun save(): DataFileSaver<T> =
        save(config)

    override fun get(): T {
        return config
    }
    override fun set(data: T): DataFileSaver<T> {
        config = data
        save()
        return this
    }
}