plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)
}

val modulePackageName = "org.gabrielsantana.quicknotes.data.task"

kotlin {

    androidLibrary {
        namespace = modulePackageName
        compileSdk = 35
        minSdk = 24

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

        getByName("androidDeviceTest").dependencies {
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