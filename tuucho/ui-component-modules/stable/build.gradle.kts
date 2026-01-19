plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core.umbrella"))

            implementation(libs.kotlin.serialization.json)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.koin.core)
            implementation(libs.coil.core)
            implementation(libs.kotlin.collections.immutable)


            implementation("io.coil-kt.coil3:coil-compose:3.3.0")


        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.tooling.preview)
}
