plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core.domain.tool"))
            implementation(project(":core.domain.business"))
            implementation(project(":core.presentation.tool"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(libs.kotlin.collections.immutable)
        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.tooling.preview)
}