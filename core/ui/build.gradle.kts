plugins {
    alias(libs.plugins.convention.library.ui)
}

android {
    namespace = "com.tezov.tuucho.core.ui"

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
            isStatic = true
            baseName = "CoreUiFramework"
            freeCompilerArgs += listOf(
                "-Xbinary=bundleId=com.tezov.tuucho.core.ui",
            )
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)

            implementation(compose.ui)
            implementation(compose.material3)


        }
    }
}
