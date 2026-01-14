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

dependencies {
    mockImplementation(libs.compose.ui.tooling)
    mockImplementation(libs.compose.ui.tooling.preview)

    devImplementation(libs.compose.ui.tooling)
    devImplementation(libs.compose.ui.tooling.preview)


}