package com.tezov.tuucho.project

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

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
        val flavorCapitalized = flavorCapitalized()
        gradle.afterProject {
            if (extra.has("hasAssets")) {
                extensions.configure(CommonExtension::class.java) {
                    sourceSets["main"].assets.srcDirs(
                        "src/commonMain/assets",
                        "src/commonMain$flavorCapitalized/assets",
                    )
                }
            }
        }
    }

}


