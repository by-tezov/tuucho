import java.util.Properties

plugins {
    alias(libs.plugins.convention.library.ui)
}

android {

    val configPropertiesFile = project.file("../config.properties")
    if (!configPropertiesFile.exists()) {
        error("⚠️ No config.properties found")
    }

    with(Properties()) {
        load(configPropertiesFile.inputStream())

        val localDatabaseFile = getProperty("localDatabaseFile")
            ?: error("Missing property: localDatabaseFile in config.properties")

        val serverUrlAndroid = getProperty("serverUrlAndroid")
            ?: error("Missing property: serverUrlAndroid in config.properties")

        buildTypes {
            getByName("prod") {
                buildConfigField("String", "localDatabaseFile", localDatabaseFile)
                buildConfigField("String", "serverUrl", serverUrlAndroid)
            }
            getByName("stage") {
                buildConfigField("String", "localDatabaseFile", localDatabaseFile)
                buildConfigField("String", "serverUrl", serverUrlAndroid)
            }
            getByName("dev") {
                buildConfigField("String", "localDatabaseFile", localDatabaseFile)
                buildConfigField("String", "serverUrl", serverUrlAndroid)
            }
            getByName("mock") {
                buildConfigField("String", "localDatabaseFile", localDatabaseFile)
                buildConfigField("String", "serverUrl", serverUrlAndroid)
            }
        }

    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {

        }
        iosMain.dependencies {

        }
        commonMain.dependencies {
            implementation(project(":barrel"))
            implementation(project(":core:presentation:ui"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}