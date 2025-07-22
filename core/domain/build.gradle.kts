import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.convention.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tezov.tuucho.core.domain"

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

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
        }
    }
}
