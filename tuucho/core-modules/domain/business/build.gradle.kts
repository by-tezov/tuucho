plugins {
    alias(libs.plugins.convention.library.plain)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core.domain.tool"))
            implementation(project(":core.domain.test"))
            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
        }
    }
}
