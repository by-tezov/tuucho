plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core.umbrella"))

            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.koin.core)
            implementation(libs.kotlin.collections.immutable)
        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.tooling.preview)
}
