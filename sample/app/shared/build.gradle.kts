import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.TargetConfigDsl
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

    val props = Properties().apply {
        load(configPropertiesFile.inputStream())
    }

    fun TargetConfigDsl.field(propertyKey: String, type: FieldSpec.Type, key: String? = null) {
        val property = props.getProperty(propertyKey)
            ?: error("Missing property: $propertyKey in config.properties")
        buildConfigField(type, key ?: propertyKey, property)
    }

    defaultConfigs {
        field("localDatabaseFileName", FieldSpec.Type.STRING)
        field("localDatastoreFileName", FieldSpec.Type.STRING)

        field("serverTimeoutMillis", FieldSpec.Type.LONG)
        field("serverVersion", FieldSpec.Type.STRING)
        field("serverHealthEndpoint", FieldSpec.Type.STRING)
        field("serverResourceEndpoint", FieldSpec.Type.STRING)
        field("serverSendEndpoint", FieldSpec.Type.STRING)
    }

    targetConfigs {
        create("android") {
            field("headerPlatformAndroid", FieldSpec.Type.STRING, "headerPlatform")
            field("serverBaseUrlAndroid", FieldSpec.Type.STRING, "serverBaseUrl")
        }

        create("ios") {
            field("headerPlatformIos", FieldSpec.Type.STRING, "headerPlatform")
            field("serverBaseUrlIos", FieldSpec.Type.STRING, "serverBaseUrl")
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

            implementation(libs.ktor.core)
        }
    }
}