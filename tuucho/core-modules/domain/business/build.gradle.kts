plugins {
    alias(libs.plugins.convention.library.plain)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core.domain.tool"))
            api(project(":core.domain.test"))
            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
        }
    }
}
