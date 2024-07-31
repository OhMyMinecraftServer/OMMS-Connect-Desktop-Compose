import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven {
        url = uri("https://maven.takeneko.icu/releases")
    }
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.components.resources)
    implementation(compose.material3)

    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.omms.client.core)

    runtimeOnly(libs.slf4j.api)
    runtimeOnly(libs.slf4j.jdk14)
}

compose.desktop {
    application {
        mainClass = "cn.mercury9.omms.connect.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "OMMS-Connect-Desktop-Compose"
            packageVersion = "1.0.0"
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "cn.mercury9.omms.connect.desktop.resources"
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
}
