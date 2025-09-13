plugins {
    alias(libs.plugins.convention.library)
    alias(libs.plugins.sql.delight)
    alias(libs.plugins.kotlin.serialization)
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

sqldelight {
    databases {
        create("Database") {
            packageName.set("${android.namespace}.database")
        }
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.okhttp)
            implementation(libs.sql.delight.driver.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.darwin)
            implementation(libs.sql.delight.driver.ios)
            implementation(libs.kotlin.couroutine)
        }
        commonMain.dependencies {
            implementation(project(":core:domain:business"))
            implementation(project(":core:domain:tool"))
            implementation(project(":core:domain:test"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.datetime)

            implementation(libs.koin.core)

            implementation(libs.ktor.core)
            implementation(libs.ktor.cio)
            implementation(libs.ktor.serialization)

            implementation(libs.sql.delight.runtime)
            implementation(libs.sql.delight.coroutines)
        }
        commonTest.dependencies {

        }
    }
}
