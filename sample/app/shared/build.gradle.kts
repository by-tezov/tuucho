import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.convention.library.ui)
    alias(libs.plugins.build.konfig)
}

buildkonfig {
    packageName = android.namespace

    val configPropertiesFile = project.file("../../config.properties")
    if (!configPropertiesFile.exists()) {
        error("⚠️ No config.properties found")
    }
    with(Properties()) {
        load(configPropertiesFile.inputStream())

        val localDatabaseFile = getProperty("localDatabaseFile")
            ?: error("Missing property: localDatabaseFile in config.properties")

        val localDatastoreFile = getProperty("localDatastoreFile")
            ?: error("Missing property: localDatastoreFile in config.properties")

        val serverUrlAndroid = getProperty("serverUrlAndroid")
            ?: error("Missing property: serverUrlAndroid in config.properties")

        val serverUrlIos = getProperty("serverUrlIos")
            ?: error("Missing property: serverUrlIos in config.properties")

        defaultConfigs {
            buildConfigField(FieldSpec.Type.STRING, "localDatabaseFile", localDatabaseFile)
            buildConfigField(FieldSpec.Type.STRING, "localDatastoreFile", localDatastoreFile)
        }

        targetConfigs {
            create("android") {
                buildConfigField(FieldSpec.Type.STRING, "serverUrl", serverUrlAndroid)
            }

            create("ios") {
                buildConfigField(FieldSpec.Type.STRING, "serverUrl", serverUrlIos)
            }
        }
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {

        }
        val isMacOs = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)
        if (isMacOs) {
            iosMain.dependencies {

            }
        }
        commonMain.dependencies {
            implementation(libs.tuucho)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}