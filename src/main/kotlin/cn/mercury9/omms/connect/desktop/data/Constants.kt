package cn.mercury9.omms.connect.desktop.data

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Constants {
    object AppInfo {
        const val VERSION = "1.1.4"
        const val CORE_VERSION = "1.5.0"
        object Github {
            const val REPO_NAME = "OMMS-Connect-Desktop-Compose"
            const val REPO_OWNER = "OhMyMinecraftServer"
            const val REPO_URL = "https://github.com/$REPO_OWNER/$REPO_NAME"
            const val REPO_RELEASES_URL = "$REPO_URL/releases"
            const val REPO_LATEST_RELEASE_URL = "$REPO_RELEASES_URL/latest"
        }
    }
}
