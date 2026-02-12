import com.tezov.tuucho.convention.project._system.isMacOs

plugins {
    alias(libs.plugins.convention.library.plain)
    alias(libs.plugins.sql.delight)
    alias(libs.plugins.kotlin.serialization)
}

sqldelight {
    databases {
        create("Database") {
            packageName = "${kotlin.androidLibrary.namespace}.database"
            srcDirs.setFrom("src/commonMain/sqldelight")
            dialect(
                libs.sql.delight.dialect.get().toString().replace(
                    oldValue = "{{version}}",
                    newValue = libs.versions.sql.delight.dialect.get()
                )
            )
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
                implementation(libs.kotlin.couroutine)
                implementation(libs.ktor.darwin)
                implementation(libs.sql.delight.driver.ios)
            }
        }
        commonMain.dependencies {
            api(project(":core.domain.business"))

            implementation(libs.kotlin.couroutine)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.datetime)
            implementation(libs.okio)
            implementation(libs.koin.core)
            implementation(libs.coil.core)
            implementation(libs.ktor.core)
            implementation(libs.ktor.cio)
            implementation(libs.ktor.serialization)
            implementation(libs.sql.delight.runtime)
            implementation(libs.sql.delight.json)
        }
    }
}
