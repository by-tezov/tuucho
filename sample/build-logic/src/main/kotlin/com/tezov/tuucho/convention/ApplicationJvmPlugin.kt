package com.tezov.tuucho.convention

import com.tezov.tuucho.convention._system.AssetHelper
import com.tezov.tuucho.convention._system.PluginId
import com.tezov.tuucho.convention._system.javaVersionInt
import com.tezov.tuucho.convention._system.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

@Suppress("unused")
class ApplicationJvmPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        applyPlugins(project)
        configure(project)
    }

    private fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.kotlinJvm))
            pluginManager.apply(plugin(PluginId.compose))
            pluginManager.apply(plugin(PluginId.composeCompiler))
            pluginManager.apply(plugin(PluginId.koin))
        }
    }

    private fun configure(
        project: Project,
    ) {
        with(project) {
            configureApplication()
            configureAssets()
        }
    }

    private fun Project.configureApplication() {
        extensions.configure(KotlinJvmProjectExtension::class.java) {
            jvmToolchain(javaVersionInt())
        }
    }

    private fun Project.configureAssets() {
        extensions.configure(SourceSetContainer::class.java) {
            named("main") {
                resources.srcDir(
                    project.layout.buildDirectory.dir("generated/tuucho").get().asFile.path
                )
            }
        }
        AssetHelper.run {
            registerTask(
                taskName = "syncJvmAssets",
                appDir = { layout.buildDirectory.dir("generated/tuucho").get().asFile },
                attachToTask = "processResources"
            )
        }
    }
}




