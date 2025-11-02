plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain:__core.domain__tool"))
            implementation(project(":core:domain:__core.domain__business"))
            implementation(project(":core:presentation:__core.presentation__tool"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
            // implementation(libs.koin.compose)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(libs.kotlin.collections.immutable)
        }
    }
}
