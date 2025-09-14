plugins {
    alias(libs.plugins.convention.application)
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

dependencies {
    implementation(project(":app:kmm"))
    implementation(project(":core:data:repository"))
    implementation(libs.compose.activity)
    implementation(libs.koin.core)
}