package com.tezov.tuucho.convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class SharedLibraryPlugin : AbstractLibraryPlugin() {

    override fun optIn() = listOf<String>(

    ).asIterable()

    override fun compilerOption() = listOf(
        "-Xcontext-parameters", // Needed by Tuucho
        "-Xexpect-actual-classes", // Needed by BuildKonfig
    )

    override val hasAssets = true

    override fun configure(
        project: Project,
    ) {
        super.configure(project)
        with(project) {
            configureBuildKonfig()
        }
    }

    private fun Project.configureBuildKonfig() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            applyDefaultHierarchyTemplate()
        }
    }
}

