package cn.mercury9.omms.connect.desktop.ui.theme

import com.jthemedetecor.OsThemeDetector

object DarkThemeDetector {
    private val detector = OsThemeDetector.getDetector()

    val isDarkTheme: Boolean
        get() = detector.isDark

    fun registerListener(listener: (Boolean) -> Unit) {
        detector.registerListener(listener)
    }
}
