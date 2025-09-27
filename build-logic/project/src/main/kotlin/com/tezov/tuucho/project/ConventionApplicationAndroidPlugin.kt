package com.tezov.tuucho.project

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import java.util.Properties

class ConventionApplicationAndroidPlugin : AbstractConventionPlugin() {

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidApplication))
            pluginManager.apply(plugin(PluginId.koltinAndroid))
            pluginManager.apply(plugin(PluginId.compose))
            pluginManager.apply(plugin(PluginId.composeCompiler))
        }
    }

    override fun configure(
        project: Project,
    ) {
        configureApplication(project)
        configureProguard(project)
        configureAssets(project)
        configureSigning(project)
//            packaging {
//                resources {
//                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
//                    excludes += "/META-INF/LICENSE.md"
//                    excludes += "/META-INF/LICENSE-notice.md"
//                    excludes += "/META-INF/*.kotlin_module"
//                }
//            }
    }

    private fun configureApplication(
        project: Project,
    ) = with(project) {
        extensions.configure(ApplicationExtension::class.java) {
            namespace = namespace()

            defaultConfig {
                applicationId = namespace
                targetSdk = targetSdk()
                versionCode = versionCode()
                versionName = versionName()
            }
        }
    }

    private fun configureProguard(project: Project) = with(project) {
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

    private fun configureAssets(androidProject: Project) = with(androidProject) {
        val buildTypeCapitalized = buildTypeCapitalized()
        gradle.afterProject {
            if (extra.has("hasAssets") && extra.get("hasAssets") == true) {
                extensions.configure(CommonExtension::class.java) {
                    sourceSets["main"].assets.srcDirs(
                        "src/commonMain/assets",
                        "src/commonMain$buildTypeCapitalized/assets",
                    )
                }
            }
        }
    }

    private fun configureSigning(
        project: Project,
    ) = with(project) {
        val keystorePropertiesFile = rootProject.file(keystorePropertiesFilePath())
        if (!keystorePropertiesFile.exists()) {
            println("⚠️ No keystore.properties found. Signing will be skipped.")
            return@with
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
                        keyAlias = getProperty("keyAliasDev")
                            ?: error("Missing property: keyAliasDev")
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

}


