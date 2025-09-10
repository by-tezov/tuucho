plugins {
    alias(libs.plugins.convention.library.ui)
}

android {
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
            implementation(project(":core:domain:business"))
            implementation(project(":core:domain:tool"))
            implementation(project(":core:presentation:tool"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(libs.kotlin.collections.immutable)
        }
    }
}
