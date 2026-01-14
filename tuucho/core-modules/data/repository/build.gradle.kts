import com.tezov.tuucho.convention.project.isMacOs

plugins {
    alias(libs.plugins.convention.library.plain)
    alias(libs.plugins.sql.delight)
    alias(libs.plugins.kotlin.serialization)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("${android.namespace}.database")
        }
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.okhttp)
            implementation(libs.sql.delight.driver.android)
            implementation(libs.datastore.preferences)
        }
        val isMacOs = isMacOs
        if (isMacOs) {
            iosMain.dependencies {
                implementation(libs.ktor.darwin)
                implementation(libs.sql.delight.driver.ios)
                implementation(libs.kotlin.couroutine)
            }
        }
        commonMain.dependencies {
            implementation(project(":core.domain.test"))
            implementation(project(":core.domain.tool"))
            implementation(project(":core.domain.business"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.datetime)

            implementation(libs.koin.core)

            implementation(libs.ktor.core)
            implementation(libs.ktor.cio)
            implementation(libs.ktor.serialization)

            implementation(libs.sql.delight.runtime)
            implementation(libs.sql.delight.coroutines)

            implementation(libs.okio)
        }
    }
}
