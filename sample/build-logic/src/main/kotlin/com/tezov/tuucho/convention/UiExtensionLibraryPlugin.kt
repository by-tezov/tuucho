package com.tezov.tuucho.convention

import com.tezov.tuucho.convention._system.LibraryId
import com.tezov.tuucho.convention._system.library
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class UiExtensionLibraryPlugin : AbstractLibraryPlugin() {

    override fun optIn() = listOf<String>(

    ).asIterable()

    override fun compilerOption() = listOf(
        "-Xcontext-parameters", // Needed by Tuucho
    )

    override val hasAssets = false

    override fun configure(
        project: Project,
    ) {
        super.configure(project)
        with(project) {
            configurePreviewDependencies()
            configurePreviewSourceSet()
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

