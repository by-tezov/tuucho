plugins {
    alias(libs.plugins.convention.benchmark)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
//            api(project(":core.domain.business"))


        }
        jvmMain.dependencies {

        }
    }
}
