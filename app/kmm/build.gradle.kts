plugins {
    alias(libs.plugins.convention.library.ui)
//    alias(libs.plugins.kotlin.serialization)
//    alias(libs.plugins.ksp)
}

android {
    namespace = "com.tezov.tuucho.kmm"

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
        androidMain.dependencies {

        }

        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:ui"))
            implementation(project(":core:data"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.components.resources)

            implementation(libs.koin.core)
        }

        iosMain.dependencies {

        }
    }
}
