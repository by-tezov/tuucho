import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.TargetConfigDsl
import com.tezov.tuucho.convention.isMacOs
import java.util.Properties

plugins {
    alias(libs.plugins.convention.shared.library)
    alias(libs.plugins.build.konfig)
}

buildkonfig {
    packageName = android.namespace

    val configPropertiesFile = project.file("../../config.properties")
    if (!configPropertiesFile.exists()) {
        error("No config.properties found")
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
        field("imageDiskCacheSizeMo", FieldSpec.Type.INT)
        field("imageDiskCacheDirectory", FieldSpec.Type.STRING)
        field("serverImageTimeoutMillis", FieldSpec.Type.LONG)
        field("serverHttpTimeoutMillis", FieldSpec.Type.LONG)
        field("serverVersion", FieldSpec.Type.STRING)
        field("serverHealthEndpoint", FieldSpec.Type.STRING)
        field("serverResourceEndpoint", FieldSpec.Type.STRING)
        field("serverSendEndpoint", FieldSpec.Type.STRING)
        field("serverImageEndpoint", FieldSpec.Type.STRING)
    }

    targetConfigs {
        create("android") {
            field("localDatastoreFileName", FieldSpec.Type.STRING)
            field("headerPlatformAndroid", FieldSpec.Type.STRING, "headerPlatform")
            field("serverBaseUrlAndroid", FieldSpec.Type.STRING, "serverBaseUrl")
        }

        create("ios") {
            field("localDatastoreFileName", FieldSpec.Type.STRING)
            field("headerPlatformIos", FieldSpec.Type.STRING, "headerPlatform")
            field("serverBaseUrlIos", FieldSpec.Type.STRING, "serverBaseUrl")
        }
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.okhttp)
        }
        if (isMacOs) {
            iosMain.dependencies {
                implementation(libs.ktor.darwin)
            }
        }
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(libs.tuucho)
            implementation(libs.tuucho.ui)
            implementation(project(":app:uiExtension"))

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)

            implementation(libs.kotlin.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.ktor.core)
        }
    }
}