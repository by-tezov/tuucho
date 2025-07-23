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
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = true
            baseName = "CoreDomainFramework"
            freeCompilerArgs += listOf(
                "-Xbinary=bundleId=com.tezov.tuucho.core.domain",
            )
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
        }
    }
}
