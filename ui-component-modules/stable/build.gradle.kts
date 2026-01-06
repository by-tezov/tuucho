plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core.domain.test"))
            api(project(":core.domain.tool"))
            api(project(":core.domain.business"))
            api(project(":core.data.repository"))
            api(project(":core.presentation.tool"))
            api(project(":core.presentation.ui"))

            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(libs.koin.core)
            implementation(libs.kotlin.collections.immutable)
        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.tooling.preview)
}