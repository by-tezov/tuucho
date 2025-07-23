import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.convention.library)
    alias(libs.plugins.sql.delight)
}

android {
    namespace = "com.tezov.tuucho.core.data"

    defaultConfig {

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("${android.namespace}.database")
        }
    }
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = true
            baseName = "CoreDataFramework"
            freeCompilerArgs += listOf(
                "-Xbinary=bundleId=com.tezov.tuucho.core.data"
            )
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.ktor.okhttp)
            implementation(libs.sql.delight.driver.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.darwin)
            implementation(libs.sql.delight.driver.ios)
        }

        commonMain.dependencies {
            implementation(project(":core:domain"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)

            implementation(libs.koin.core)

            implementation(libs.ktor.core)
            implementation(libs.ktor.cio)
            implementation(libs.ktor.serialization)

            implementation(libs.sql.delight.runtime)
            implementation(libs.sql.delight.coroutines)

        }

    }
}
