plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:data:core-data-repository"))
        }
        iosMain.dependencies {
            implementation(project(":core:data:core-data-repository"))
        }
        commonMain.dependencies {
            implementation(project(":core:domain:core-domain-test"))
            implementation(project(":core:domain:core-domain-tool"))
            implementation(project(":core:domain:core-domain-business"))
            implementation(project(":core:data:core-data-repository"))
            implementation(project(":core:presentation:core-presentation-tool"))
            implementation(project(":core:presentation:core-presentation-ui"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}
