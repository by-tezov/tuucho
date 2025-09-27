plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain:core-domain-tool"))
            implementation(project(":core:domain:core-domain-business"))
            implementation(project(":core:presentation:core-presentation-tool"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(libs.kotlin.collections.immutable)
        }
    }
}
