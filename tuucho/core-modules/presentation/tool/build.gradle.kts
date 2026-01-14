plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core.domain.tool"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.kotlin.collections.immutable)
        }
    }
}
