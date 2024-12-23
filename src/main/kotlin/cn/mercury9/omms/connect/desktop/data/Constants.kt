package cn.mercury9.omms.connect.desktop.data

import org.jetbrains.skiko.hostOs
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.pathString

@Suppress("MemberVisibilityCanBePrivate")
object Constants {
    object AppInfo {
        const val PACKAGE_NAME = "cn.mercury9.omms.connect.desktop"
        const val VERSION = "1.2.0"
        const val CORE_VERSION = "1.7.0"
        object Github {
            const val REPO_NAME = "OMMS-Connect-Desktop-Compose"
            const val REPO_OWNER = "OhMyMinecraftServer"
            const val REPO_URL = "https://github.com/$REPO_OWNER/$REPO_NAME"
            const val REPO_RELEASES_URL = "$REPO_URL/releases"
            const val REPO_LATEST_RELEASE_URL = "$REPO_RELEASES_URL/latest"
        }
    }
    object Data {
        val CONFIG_DIR =
            Path(System.getProperty("user.home"))
                .resolve(
                    if (hostOs.isMacOS) {
                        ".config/${AppInfo.PACKAGE_NAME}"
                    } else {
                        ".OmmsConnectDesktop"
                    }
                ).also {
                    it.createDirectories()
                }.pathString.also { println(it) }

        val CACHE_DIR =
            Path(System.getProperty("user.home"))
                .resolve(
                    if (hostOs.isMacOS) {
                        "Library/Caches/${AppInfo.PACKAGE_NAME}"
                    } else if (hostOs.isWindows) {
                        ".OmmsConnectDesktop/.cache"
                    } else {
                        ".cache/${AppInfo.PACKAGE_NAME}"
                    }
                )
                .also {
                    it.createDirectories()
                }.pathString.also { println(it) }
    }
}
