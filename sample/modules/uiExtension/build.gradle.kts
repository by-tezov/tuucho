plugins {
    alias(libs.plugins.convention.ui.extension.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.tuucho.umbrella.android)
        }
        commonMain.dependencies {
            implementation(libs.tuucho.umbrella)

            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.kotlin.collections.immutable)

            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
        }
    }
}
