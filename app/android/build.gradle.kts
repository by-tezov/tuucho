import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.convention.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.tezov.tuucho.demo"

    defaultConfig {
        applicationId = "com.tezov.tuucho.demo"
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

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:data"))
            implementation(project(":core:ui"))

            implementation(libs.androidx.core.ktx)
            implementation(libs.kotlin.serialization.json)

            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.lifecycle.livedata.ktx)
            implementation(libs.androidx.lifecycle.viewmodel.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)

            implementation(libs.compose.ui)
            implementation(libs.compose.viewmodel)
            implementation(libs.compose.lifecycle)
            implementation(libs.compose.activity)
            implementation(libs.compose.material)

            implementation(libs.koin.core)
            implementation(libs.koin.android)
            implementation(libs.koin.compose)

            implementation(libs.ktor.core)
            implementation(libs.ktor.okhttp)
            implementation(libs.ktor.cio)
            implementation(libs.ktor.serialization)

            implementation(libs.sql.delight.runtime)
            implementation(libs.sql.delight.driver)
            implementation(libs.sql.delight.coroutines)

        }
    }
}
