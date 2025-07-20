plugins {
    alias(libs.plugins.convention.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sql.delight)
}

android {
    namespace = "com.tezov.tuucho.core.data"

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

sqldelight {
    databases {
        create("Database") {
            packageName.set("${android.namespace}.database")
        }
    }
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.serialization.json)

    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.ktor.core)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.serialization)
    debugImplementation(libs.ktor.logging)

    implementation(libs.sql.delight.runtime)
    implementation(libs.sql.delight.driver)
    implementation(libs.sql.delight.coroutines)

    testImplementation(libs.junit)
}