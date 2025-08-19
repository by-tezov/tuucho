plugins {
    alias(libs.plugins.convention.library.ui)
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
        iosMain.dependencies {

        }
        commonMain.dependencies {
            implementation(project(":core:domain:business"))
            implementation(project(":core:domain:tool"))
            implementation(project(":core:presentation:ui"))
            implementation(project(":core:data"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.components.resources)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}
