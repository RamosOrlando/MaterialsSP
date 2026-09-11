import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.screenshot)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    androidLibrary {
       namespace = "com.materials.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
       
       experimentalProperties["android.experimental.enableScreenshotTest"] = true
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.core.ktx)
            // Koin
            implementation(libs.koin.android)
            
            // Screenshot Testing Support
            implementation(libs.screenshot.validation.api)
        }
        commonMain.dependencies {
            implementation(project(":core:network"))
            implementation(project(":core:database"))
            implementation(project(":core:common"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            // Koin
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            // Navigation 3 for CMP
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodel.nav3)
            implementation(libs.jetbrains.lifecycle.viewmodel)
            // Extended icons
            implementation(libs.compose.icons.extended)
            // Coil for images and MediaPlayer
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
            implementation(libs.compose.multiplatform.media.player)
            // Adaptative interface
            implementation(libs.material3.adaptive.layout)
            implementation(libs.material3.adaptive.navigation)
            // Datetime, Serialization Json for KMP
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            // Persistent storage in KMP, for small data
            implementation(libs.multiplatformSettings)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
        }
        jvmMain.dependencies {
        }
    }
}

