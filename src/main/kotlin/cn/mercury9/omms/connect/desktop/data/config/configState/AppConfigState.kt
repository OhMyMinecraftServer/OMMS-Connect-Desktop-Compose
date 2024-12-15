package cn.mercury9.omms.connect.desktop.data.config.configState

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cn.mercury9.omms.connect.desktop.data.Constants
import cn.mercury9.omms.connect.desktop.data.config.AppConfig
import cn.mercury9.omms.connect.desktop.data.config.OmmsServerListSortBy
import cn.mercury9.omms.connect.desktop.data.saver.DataFileSaver
import kotlin.io.path.Path

object AppConfigState: SavableData<AppConfig> {
    override val dataSaver = DataFileSaver(
        Path(Constants.Data.CONFIG_DIR).resolve("config.json"),
        ::AppConfig,
        AppConfig.serializer()
    )

    lateinit var setupThemeBySystemDarkTheme: MutableState<Boolean>
    lateinit var ommsServerListSortBy: MutableState<OmmsServerListSortBy>

    init {
        loadFromConfigFile()
    }

    override fun loadFromConfigFile() {
        val appConfig = dataSaver.get()

        setupThemeBySystemDarkTheme = mutableStateOf(appConfig.setupThemeBySystemDarkTheme)
        ommsServerListSortBy = mutableStateOf(appConfig.ommsServerListSortBy)
    }

    override fun asConfigData(): AppConfig =
        AppConfig(
            setupThemeBySystemDarkTheme = setupThemeBySystemDarkTheme.value,
            ommsServerListSortBy = ommsServerListSortBy.value,
        )

    override fun saveToConfigFile() {
        dataSaver.set(asConfigData())
    }
}