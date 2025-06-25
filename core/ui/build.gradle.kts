plugins {
    alias(libs.plugins.convention.library.ui)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tezov.tuucho.core.ui"

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
    implementation(project(":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.koin.core)

    implementation(libs.compose.ui)
    implementation(libs.compose.lifecycle)
    implementation(libs.compose.navigation)
    implementation(libs.compose.activity)
    implementation(libs.compose.material)

    testImplementation(libs.junit)
}