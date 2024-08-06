package cn.mercury9.omms.connect.desktop.data.configs

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class Data<T : Any>(
    val filePath: Path,
    private val defaultConfig: T,
    private val serializer: KSerializer<T>,
    private val stringFormat: StringFormat = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    },
) {

    private var config: T = load()

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
                stringFormat.decodeFromString(serializer, filePath.readText()).also {
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
        filePath.writeText(stringFormat.encodeToString(serializer, config?:defaultConfig))
    }
    fun write() {
        write(config)
    }

    fun get(): T {
        return config
    }
    fun set(value: T) {
        config = value
        write(value)
        callOnConfigChange(value)
    }
}