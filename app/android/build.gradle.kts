plugins {
    alias(libs.plugins.convention.application)
}

android {
    namespace = "com.tezov.tuucho.demo"

    defaultConfig {
        applicationId = "com.tezov.demo.tuucho.android"
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

dependencies {
    implementation(project(":app:kmm"))
    implementation(project(":core:data"))
    implementation(libs.compose.activity)
    implementation(libs.koin.core)
}