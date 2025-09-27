plugins {
    alias(libs.plugins.convention.library.ui)
    alias(libs.plugins.convention.maven)
}

kotlin {
    sourceSets {
        androidMain.dependencies {

        }
        iosMain.dependencies {

        }
        commonMain.dependencies {
            implementation(project(":core:domain:business"))
            implementation(project(":core:domain:tool"))
            implementation(project(":core:presentation:ui"))
            implementation(project(":core:presentation:tool"))
            implementation(project(":core:data:repository"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}
