import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)
}

val modulePackageName = "org.gabrielsantana.quicknotes.data.task"

kotlin {

    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
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
            baseName = "CoreData"
            isStatic = true
        }
        iosTarget.binaries.all {
            linkerOpts("-lsqlite3")
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.koin.android.workmanager)
            implementation(libs.androidx.startup.runtime)
            // Kotlin + coroutines
            implementation(libs.androidx.work.runtime.ktx)
            // optional - GCMNetworkManager support
            implementation(libs.androidx.work.gcm)
            // optional - Multiprocess support
            implementation(libs.androidx.work.multiprocess)
        }

        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            // Add KMP dependencies here
            implementation(libs.sqldelight.coroutines)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.datastore.preferences)
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.realtime)
            implementation(libs.kermit)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.multiplatform.settings.serialization)
            implementation(libs.multiplatform.settings.datastore)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.test.junit)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }
    }

    sqldelight {
        databases {
            create("TaskDatabase") {
                packageName = modulePackageName
            }
        }
    }

}

android {
    namespace = modulePackageName
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    testImplementation(libs.junit.jupiter)
}