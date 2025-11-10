plugins {
    alias(libs.plugins.convention.library.plain)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.datetime)
            implementation(libs.koin.core)
        }
    }
}
