package com.tezov.tuucho.project

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import java.util.Properties

open class ConventionApplicationAndroidPlugin : ConventionPlugin() {

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
        configureAndroidApplication(project)
        configureCompose(project)
        configureAndroidAssets(project)
        configureAndroidSigning(project)
//            packaging {
//                resources {
//                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
//                    excludes += "/META-INF/LICENSE.md"
//                    excludes += "/META-INF/LICENSE-notice.md"
//                    excludes += "/META-INF/*.kotlin_module"
//                }
//            }
    }

    private fun configureAndroidApplication(
        project: Project,
    ) = with(project) {
        extensions.configure(ApplicationExtension::class.java) {
            namespace = namespace()

            defaultConfig {
                applicationId = "${namespace()}.android"
                targetSdk = targetSdk()
                versionCode = versionCode()
                versionName = versionName()
            }
        }
        project.extensions.configure(KotlinAndroidProjectExtension::class.java) {
            jvmToolchain(this@with.javaVersionInt())
            compilerOptions.jvmTarget.set(this@with.jvmTarget())
            compilerOptions.optIn.configureOptIn()
            compilerOptions.allWarningsAsErrors.set(true)
        }
    }

    private fun configureAndroidAssets(androidProject: Project) = with(androidProject) {
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

    private fun configureAndroidSigning(
        project: Project,
    ) = with(project) {
        val keystorePropertiesFile = rootProject.file(keystorePropertiesFilePath())
        if (keystorePropertiesFile.exists()) {
            println("⚠️ No keystore.properties found. Signing will be skipped.")
            return@with
        }
        extensions.configure(ApplicationExtension::class.java) {
            signingConfigs {
                with(Properties()) {
                    load(keystorePropertiesFile.inputStream())
                    val storePath = getProperty("keystorePath")
                        ?: error("Missing property: keystorePath in keystore.properties")
                    val storeFile = file(storePath)
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
                        keyPassword = getProperty("keyPasswordProd")
                            ?: error("Missing property: keyPasswordProd")
                    }

                    create("stage") {
                        this.storeFile = storeFile
                        this.storePassword = storePassword
                        keyAlias = getProperty("keyAliasStage")
                            ?: error("Missing property: keyAliasStage")
                        keyPassword = getProperty("keyPasswordStage")
                            ?: error("Missing property: keyPasswordStage")
                    }

                    create("dev") {
                        this.storeFile = storeFile
                        this.storePassword = storePassword
                        keyAlias = getProperty("keyAliasDev")
                            ?: error("Missing property: keyAliasDev")
                        keyPassword = getProperty("keyPasswordDev")
                            ?: error("Missing property: keyPasswordDev")
                    }

                    create("mock") {
                        this.storeFile = storeFile
                        this.storePassword = storePassword
                        keyAlias = getProperty("keyAliasDev")
                            ?: error("Missing property: keyAliasDev")
                        keyPassword = getProperty("keyPasswordDev")
                            ?: error("Missing property: keyPasswordDev")
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


