import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.org.apache.commons.io.output.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

group = "cn.mercury9.omms.connect.desktop"
version = "1.1.3"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven {
        name = "NekoMaven"
        url = uri("https://maven.takeneko.icu/releases")
    }
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }
    implementation(compose.components.resources)
    implementation(compose.material3)

    implementation(libs.compose.navigation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.timedate)

    api(libs.imageLoader.core)
    api(libs.imageLoader.composeResources)
    api(libs.imageLoader.imageio)

    implementation(libs.haze)
    implementation(libs.haze.materials)

    implementation(libs.omms.client.core)
    implementation(libs.gson)

}

compose.desktop {
    application {
        mainClass = "$group.MainKt"

        nativeDistributions {

            modules(
                "java.instrument",
                "java.management",
                "java.sql",
                "jdk.unsupported",
                "java.xml",
            )

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "OMMS Connect Desktop"
            packageVersion = "$version"
            copyright = "© 2024 OhMyMinecraftServer"
            licenseFile.set(project.file("LICENSE"))

            windows {
                iconFile.set(project.file("icon.ico"))
                dirChooser = true
                menuGroup = "OhMyMinecraftServer"
            }

            macOS {
                iconFile.set(project.file("icon.icns"))
            }

            linux {
                iconFile.set(project.file("icon.png"))
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "$group.resources"
    generateResClass = always
}

tasks {
    val replaceAndroidColorInVectorDrawables by creating {
        doLast {
            val drawableFileTree = fileTree("src/main/composeResources/drawable") {
                include("*.xml")
            }
            var vectorDrawableFilesProcessedCount = 0
            drawableFileTree.forEach {
                if (it.readLines()[0].startsWith("<vector")) {
                    println("Processing: ${it.name}")
                    val oldContent = it.readText()
                    val newContent = oldContent.replace(
                        "@android:color/white",
                        "#ffffff"
                    )
                    if (newContent != oldContent) {
                        it.writeText(newContent)
                        println(" - Processed.")
                        vectorDrawableFilesProcessedCount++
                    } else {
                        println(" - This asset does not need to modify.")
                    }
                }
            }
            println("Process finished, $vectorDrawableFilesProcessedCount file(s) processed.")
        }
    }.apply {
        description =
            "Replace \"@android:color/white\" " +
                    "in vector drawables that download from Google Icons " +
            "to \"#ffffff\""
    }

    val countKtSourceFileLinesAndWriteToREADME by creating {
        doLast {
            val out = ByteArrayOutputStream()
            val commandCountKt = "git ls-files '*.kt' | xargs cat | wc -l"

            exec {
                standardOutput = out
                workingDir = projectDir
                commandLine("sh", "-c", commandCountKt)
            }

            val ktLines = out.toString(Charsets.UTF_8).filterNot {
                it.isWhitespace()
            }
            println("$ktLines lines kt")

            val readmeFile = project.file("README.md")

            readmeFile.readText().apply {
                replace(
                    this.lines().filter {
                        it.startsWith("[![Kotlin](https://img.shields.io/badge/")
                    }[0],
                "[![Kotlin](https://img.shields.io/badge/${ktLines}_lines-Kotlin-7954F6?logo=kotlin)](https://kotlinlang.org/)"
                ).also {
                    readmeFile.writeText(it)
                }
            }

        }
    }
}
