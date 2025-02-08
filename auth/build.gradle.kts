import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "auth"
        }
    }
    jvm("desktop")
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            //Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            //ViewModel
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            //Firebase
            implementation(libs.kmpauth.google)
            implementation(libs.kmpauth.firebase)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)

        }
        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.test.junit)
        }
        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
        }
    }

}

android {
    namespace = "org.gabrielsantana.tasks.auth"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


dependencies {
    testImplementation(libs.junit.jupiter)
    debugImplementation(compose.uiTooling)
}