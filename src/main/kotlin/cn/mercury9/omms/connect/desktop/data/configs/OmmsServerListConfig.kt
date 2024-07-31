package cn.mercury9.omms.connect.desktop.data.configs

import cn.mercury9.omms.connect.desktop.data.OmmsServer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val ommsServerListConfig = OmmsServerListConfig().apply {
    loadConfig()
}

class OmmsServerListConfig(
    configFilePath: String = "./data",
    configFileName: String = "omms_servers.json",
    private val serializer: StringFormat = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    },
    configManagerFactory: ConfigManagerFactory<List<OmmsServer>>
        = DefaultConfigManager.Factory()
) {
    private val configManager = configManagerFactory.create(
        emptyList(),
        configFilePath,
        configFileName,
        { serializer.encodeToString(it) },
        { serializer.decodeFromString(it) }
    )

    var onUpdateList: (List<OmmsServer>) -> Unit = {}
    var configData: List<OmmsServer> = emptyList()
        private set(value) {
            field = value
            onUpdateList(value)
            saveConfig()
        }

    fun add(server: OmmsServer) {
        configData += server
    }

    fun loadConfig() {
        configData = configManager.loadConfig()
    }

    fun saveConfig() {
        configManager.saveConfig(configData)
    }
}