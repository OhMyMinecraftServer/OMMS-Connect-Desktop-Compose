package cn.mercury9.omms.connect.desktop.data.configs

import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

interface ConfigManager<T> {
    val defaultConfig: T
    fun ensureConfigPath(path: String) {
        Path(path).apply {
            if (!isDirectory()) {
                createDirectory()
            }
        }
    }
    fun loadConfig(): T
    fun saveConfig(config: T)
}

interface ConfigManagerFactory<T> {
    fun create(
        defaultConfig: T,
        configFilePath: String,
        configFileName: String,
        encodeToString: (T) -> String,
        decodeFromString: (String) -> T
    ): ConfigManager<T>
}

class DefaultConfigManager<T> private constructor(
    override val defaultConfig: T,
    private val configFilePath: String,
    private val configFileName: String,
    private val encodeToString: (T) -> String,
    private val decodeFromString: (String) -> T
): ConfigManager<T> {

    class Factory<T> : ConfigManagerFactory<T> {
        override fun create(
            defaultConfig: T,
            configFilePath: String,
            configFileName: String,
            encodeToString: (T) -> String,
            decodeFromString: (String) -> T
        ): DefaultConfigManager<T> {
            return DefaultConfigManager(
                defaultConfig,
                configFilePath,
                configFileName,
                encodeToString,
                decodeFromString
            )
        }
    }

    override fun loadConfig(): T {
        ensureConfigPath(configFilePath)
        Path(configFilePath, configFileName).apply {
            return if (notExists()) {
                defaultConfig.also { saveConfig(defaultConfig) }
            } else try {
                decodeFromString(readText())
            } catch (ex: Exception) {
                defaultConfig.also { saveConfig(defaultConfig) }
            }
        }
    }

    override fun saveConfig(config: T) {
        ensureConfigPath(configFilePath)
        Path(configFilePath, configFileName).apply {
            deleteIfExists()
            createFile()
            writeText(encodeToString(config))
        }
    }
}
