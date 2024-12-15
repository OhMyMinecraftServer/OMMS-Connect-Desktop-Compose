package cn.mercury9.omms.connect.desktop.data.config.configState

import cn.mercury9.omms.connect.desktop.data.saver.DataSaver

interface SavableData<T> {
    val dataSaver: DataSaver<T>

    fun loadFromConfigFile()
    fun asConfigData(): T
    fun saveToConfigFile()
}