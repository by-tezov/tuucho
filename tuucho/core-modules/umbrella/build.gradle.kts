import com.tezov.tuucho.convention.project.isMacOs

plugins {
    alias(libs.plugins.convention.library.ui)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(project(":core.data.repository"))
        }
        if (isMacOs) {
            iosMain.dependencies {
                api(project(":core.data.repository"))
            }
        }
        commonMain.dependencies {
            api(project(":core.domain.test"))
            api(project(":core.domain.tool"))
            api(project(":core.domain.business"))
            api(project(":core.data.repository"))
            api(project(":core.presentation.tool"))
            api(project(":core.presentation.ui"))

            implementation(libs.compose.runtime)
        }
    }
}
