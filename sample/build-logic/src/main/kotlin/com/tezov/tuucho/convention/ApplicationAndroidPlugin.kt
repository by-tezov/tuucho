package com.tezov.tuucho.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.tezov.tuucho.convention._system.AssetHelper
import com.tezov.tuucho.convention._system.PluginId
import com.tezov.tuucho.convention._system.buildType
import com.tezov.tuucho.convention._system.javaVersion
import com.tezov.tuucho.convention._system.keystorePropertiesFilePath
import com.tezov.tuucho.convention._system.namespace
import com.tezov.tuucho.convention._system.plugin
import com.tezov.tuucho.convention._system.targetSdk
import com.tezov.tuucho.convention._system.version
import com.tezov.tuucho.convention._system.versionCode
import com.tezov.tuucho.convention._system.versionName
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.Properties

class ApplicationAndroidPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        applyPlugins(project)
        configure(project)
    }

    private fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidApplication))
            pluginManager.apply(plugin(PluginId.koin))
            pluginManager.apply(plugin(PluginId.compose))
            pluginManager.apply(plugin(PluginId.composeCompiler))
        }
    }

    private fun configure(
        project: Project,
    ) {
        with(project) {
            configureApplication()
            configureBuildType()
            configureProguard()
            configureSigning()
            configureAssets()
        }
    }

    private fun Project.configureApplication() {
        extensions.configure(ApplicationExtension::class.java) {
            namespace = namespace()
            compileSdk = version("compileSdk").toInt()
            defaultConfig {
                applicationId = namespace
                targetSdk = targetSdk()
                minSdk = version("minSdk").toInt()
                versionCode = versionCode()
                versionName = versionName()
            }
            compileOptions {
                sourceCompatibility = javaVersion()
                targetCompatibility = javaVersion()
            }
        }
    }

    private fun Project.configureBuildType() {
        extensions.configure(ApplicationExtension::class.java) {
            buildTypes {
                create("prod") {
                    initWith(getByName("release"))
                    matchingFallbacks += listOf("release")
                }
                create("stage") {
                    initWith(getByName("release"))
                    matchingFallbacks += listOf("release")
                }
                create("dev") {
                    initWith(getByName("debug"))
                    matchingFallbacks += listOf("debug")
                }
                create("mock") {
                    initWith(getByName("debug"))
                    matchingFallbacks += listOf("debug")
                }
            }
        }
        extensions.configure(AndroidComponentsExtension::class.java) {
            beforeVariants { builder ->
                if (builder.buildType == "debug" || builder.buildType == "release") {
                    builder.enable = false
                }
            }
        }
    }

    private fun Project.configureProguard() {
        extensions.configure(ApplicationExtension::class.java) {
            buildTypes {
                getByName("prod") {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    isDebuggable = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
        }
    }

    private fun Project.configureSigning() {
        val keystorePropertiesFile = rootProject.file(keystorePropertiesFilePath())
        if (!keystorePropertiesFile.exists()) {
            println("⚠️ No keystore.properties found. Signing will be skipped.")
            return
        }
        extensions.configure(ApplicationExtension::class.java) {
            signingConfigs {
                with(Properties()) {
                    load(keystorePropertiesFile.inputStream())
                    val storePath = getProperty("keystoreFilePath")
                        ?: error("Missing property: keystorePath in keystore.properties")
                    val storeFile = rootProject.file(storePath)
                    if (!storeFile.exists()) {
                        error("Keystore file '$storePath' not found.")
                    }
                    val storePassword = getProperty("keystorePassword")
                        ?: error("Missing property: keystorePassword")

                    create("prod") {
                        this.storeFile = storeFile
                        this.storePassword = storePassword
                        keyAlias = getProperty("keyAliasProd")
                            ?: error("Missing property: keyAliasProd")
                        keyPassword = storePassword
                    }

                    create("stage") {
                        this.storeFile = storeFile
                        this.storePassword = storePassword
                        keyAlias = getProperty("keyAliasStage")
                            ?: error("Missing property: keyAliasStage")
                        keyPassword = storePassword
                    }

                    create("dev") {
                        this.storeFile = storeFile
                        this.storePassword = storePassword
                        keyAlias = getProperty("keyAliasDev")
                            ?: error("Missing property: keyAliasDev")
                        keyPassword = storePassword
                    }

                    create("mock") {
                        this.storeFile = storeFile
                        this.storePassword = storePassword
                        keyAlias = getProperty("keyAliasMock")
                            ?: error("Missing property: keyAliasMock")
                        keyPassword = storePassword
                    }
                }
            }
            buildTypes {
                buildTypes {
                    getByName("prod") {
                        signingConfig = signingConfigs.getByName("prod")
                    }
                    getByName("stage") {
                        signingConfig = signingConfigs.getByName("stage")
                    }
                    getByName("dev") {
                        signingConfig = signingConfigs.getByName("dev")
                    }
                    getByName("mock") {
                        signingConfig = signingConfigs.getByName("mock")
                    }
                }
            }
        }
    }

    private fun Project.configureAssets() {
        val buildType = buildType()
        extensions.configure(AndroidComponentsExtension::class.java) {
            onVariants { variant ->
                if (variant.buildType == buildType) {
                    val assets = variant.sources.assets ?: error("missing assets source")
                    assets.addStaticSourceDirectory(
                        project.layout.buildDirectory.dir("generated/assets").get().asFile.path
                    )
                }
            }
        }
        AssetHelper.run {
            registerTask(
                taskName = "syncAndroidAssets",
                appDir = { layout.buildDirectory.dir("generated").get().asFile },
                attachToTask = "assemble${buildType.replaceFirstChar { it.uppercaseChar() }}"
            )
        }
    }
}



