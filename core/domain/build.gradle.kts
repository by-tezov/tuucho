plugins {
    alias(libs.plugins.convention.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tezov.tuucho.core.domain"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.koin.core)

    testImplementation(libs.junit)
}