@file:OptIn(ExperimentalAbiValidation::class)

import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(project(":core:data:__core.data__repository"))
        }
        val isMacOs = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)
        if (isMacOs) {
            iosMain.dependencies {
                api(project(":core:data:__core.data__repository"))
            }
        }
        commonMain.dependencies {
            api(project(":core:domain:__core.domain__test"))
            api(project(":core:domain:__core.domain__tool"))
            api(project(":core:domain:__core.domain__business"))
            api(project(":core:data:__core.data__repository"))
            api(project(":core:presentation:__core.presentation__tool"))
            api(project(":core:presentation:__core.presentation__ui"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}
