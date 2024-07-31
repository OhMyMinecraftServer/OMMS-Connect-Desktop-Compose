package cn.mercury9.omms.connect.desktop.data.configs

import cn.mercury9.omms.connect.desktop.ui.theme.AppTheme
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.ALWAYS
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AppConfigData(
    @EncodeDefault(ALWAYS) var setupThemeBySystemDarkTheme: Boolean = true,

    @EncodeDefault(ALWAYS) val theme: AppConfigTheme = AppConfigTheme(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AppConfigTheme(
    @EncodeDefault(ALWAYS) var themeType: ThemeType = ThemeType.Default,

    @EncodeDefault(ALWAYS) var contrastType: ContrastType = ContrastType.Default,

    @EncodeDefault(ALWAYS) var darkTheme: Boolean = true,
)

val appConfig = AppConfig().apply {
    loadConfig()
}

class AppConfig(
    configFilePath: String = "./data",
    configFileName: String = "config.json",
    private val serializer: StringFormat = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    },
    configManagerFactory: ConfigManagerFactory<AppConfigData>
        = DefaultConfigManager.Factory()
) {
    private val configManager =  configManagerFactory.create(
        AppConfigData(),
        configFilePath,
        configFileName,
        { serializer.encodeToString(it) },
        { serializer.decodeFromString(it) }
    )

    var onSetConfigTheme: (AppTheme) -> Unit = {}
    var configData: AppConfigData = AppConfigData()
        private set
    var configTheme: AppTheme
        get(): AppTheme {
            return AppTheme(
                themeType = configData.theme.themeType,
                contrastType = configData.theme.contrastType,
                darkTheme = configData.theme.darkTheme
            )
        }
        private set(appTheme) {
            configData.theme.themeType = appTheme.themeType
            configData.theme.contrastType = appTheme.contrastType
            configData.theme.darkTheme = appTheme.darkTheme
        }
    fun configTheme(
        darkTheme: Boolean = configData.theme.darkTheme,
        themeType: ThemeType = configData.theme.themeType,
        contrastType: ContrastType = configData.theme.contrastType,
    ): AppTheme {
        return configTheme.copy(
            darkTheme = darkTheme,
            themeType = themeType,
            contrastType = contrastType,
        )
    }

    fun setAppTheme(appTheme: AppTheme) {
        configTheme = appTheme
        onSetConfigTheme(appTheme)
        saveConfig()
    }
    fun setAppTheme(themeType: ThemeType) {
        configData.theme.themeType = themeType
        onSetConfigTheme(configTheme)
        saveConfig()
    }
    fun setAppTheme(contrastType: ContrastType) {
        configData.theme.contrastType = contrastType
        onSetConfigTheme(configTheme)
        saveConfig()
    }
    fun setAppTheme(darkTheme: Boolean) {
        configData.theme.darkTheme = darkTheme
        onSetConfigTheme(configTheme)
        saveConfig()
    }

    fun loadConfig() {
        configData = configManager.loadConfig()
    }

    fun saveConfig() {
        configManager.saveConfig(configData)
    }
}
