package cn.mercury9.omms.connect.desktop.data.config.configState

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cn.mercury9.omms.connect.desktop.data.Constants
import cn.mercury9.omms.connect.desktop.data.config.ThemeConfig
import cn.mercury9.omms.connect.desktop.data.saver.DataFileSaver
import cn.mercury9.omms.connect.desktop.ui.theme.ContrastType
import cn.mercury9.omms.connect.desktop.ui.theme.ThemeType
import kotlin.io.path.Path

object ThemeConfigState: SavableData<ThemeConfig> {
    override val dataSaver = DataFileSaver(
        Path(Constants.Data.CONFIG_DIR).resolve("themeConfig.json"),
        ::ThemeConfig,
        ThemeConfig.serializer()
    )

    lateinit var themeType: MutableState<ThemeType>
    lateinit var contrastType: MutableState<ContrastType>
    lateinit var darkTheme: MutableState<Boolean>

    init {
        loadFromConfigFile()
    }

    override fun loadFromConfigFile() {
        val themeConfig = dataSaver.get()

        themeType = mutableStateOf(themeConfig.themeType)
        contrastType = mutableStateOf(themeConfig.contrastType)
        darkTheme = mutableStateOf(themeConfig.darkTheme)
    }

    override fun asConfigData(): ThemeConfig =
        ThemeConfig(
            themeType = themeType.value,
            contrastType = contrastType.value,
            darkTheme = darkTheme.value,
        )

    override fun saveToConfigFile() {
        dataSaver.set(asConfigData())
    }
}