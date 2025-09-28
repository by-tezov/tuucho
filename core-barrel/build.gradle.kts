plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:data:__core.data__repository"))
        }
        iosMain.dependencies {
            implementation(project(":core:data:__core.data__repository"))
        }
        commonMain.dependencies {
            implementation(project(":core:domain:__core.domain__test"))
            implementation(project(":core:domain:__core.domain__tool"))
            implementation(project(":core:domain:__core.domain__business"))
            implementation(project(":core:data:__core.data__repository"))
            implementation(project(":core:presentation:__core.presentation__tool"))
            implementation(project(":core:presentation:__core.presentation__ui"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}
