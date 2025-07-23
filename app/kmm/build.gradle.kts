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
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KmmFramework"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.compose.lifecycle)
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
        }

        iosMain.dependencies {

        }
    }
}
