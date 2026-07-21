import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.materials.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MaterialsSP"
            packageVersion = "1.0.0"
            description = "Gestión de Materiales Industriales"
            vendor = "MaterialsSP Team"
            
            macOS {
                bundleID = "com.materials.sp"
                dockName = "MaterialsSP"
            }
            
            linux {
                shortcut = true
            }
            
            windows {
                shortcut = true
                menu = true
            }
        }
    }
}