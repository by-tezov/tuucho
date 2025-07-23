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
//    implementation(project(":core:domain"))
//    implementation(project(":core:data"))
//    implementation(project(":core:ui"))

    implementation(libs.compose.activity)
    implementation(libs.androidx.appcompat)
//    implementation(libs.lifecycle.viewmodel)
//    implementation(libs.compose.lifecycle)

//    implementation(libs.androidx.lifecycle.livedata.ktx)
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.ui)
    implementation(compose.material3)
    implementation(compose.components.resources)

//    implementation(libs.kotlin.couroutine)
//    implementation(libs.kotlin.serialization.json)

//    implementation(libs.koin.core)
//    implementation(libs.koin.android)
//    implementation(libs.koin.compose)
//
//    implementation(libs.ktor.core)
//    implementation(libs.ktor.okhttp)
//    implementation(libs.ktor.cio)
//    implementation(libs.ktor.serialization)
//
//    implementation(libs.sql.delight.runtime)
//    implementation(libs.sql.delight.driver.android)
//    implementation(libs.sql.delight.coroutines)
}