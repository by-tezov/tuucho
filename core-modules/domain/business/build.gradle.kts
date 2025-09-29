plugins {
    alias(libs.plugins.convention.library.plain)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain:__core.domain__tool"))
            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
        }
    }
}
