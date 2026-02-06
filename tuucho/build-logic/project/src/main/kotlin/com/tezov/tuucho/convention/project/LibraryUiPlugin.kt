package com.tezov.tuucho.convention.project

import com.tezov.tuucho.convention.project._system.LibraryId
import com.tezov.tuucho.convention.project._system.PluginId
import com.tezov.tuucho.convention.project._system.buildType
import com.tezov.tuucho.convention.project._system.library
import com.tezov.tuucho.convention.project._system.plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class LibraryUiPlugin : LibraryPlainPlugin() {

    private val Project.shouldConfigurePreview
        get() = buildType() == "debug"

    override fun applyPlugins(project: Project) {
        super.applyPlugins(project)
        with(project) {
            pluginManager.apply(plugin(PluginId.compose))
            pluginManager.apply(plugin(PluginId.composeCompiler))
        }
    }

    override fun configure(
        project: Project,
    ) {
        super.configure(project)
        with(project) {
            if (shouldConfigurePreview) {
                configurePreviewDependencies()
                configurePreviewSourceSet()
            }
        }
    }

    private fun Project.configurePreviewDependencies() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                androidMain.dependencies {
                    implementation(library(LibraryId.composeUiTooling))
                }
            }
        }
        dependencies {
            add(
                "androidRuntimeClasspath",
                library(LibraryId.composeUiToolingPreview)
            )
        }
    }

    private fun Project.configurePreviewSourceSet() {
        val folder = "preview"
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                androidMain {
                    kotlin.srcDirs(
                        "${project.projectDir.path}/src/androidMain/$folder"
                    )
                }
            }
        }
    }
}

