package com.tezov.tuucho.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ConventionLibraryUiPlugin : ConventionLibraryPlugin() {

    override fun applyPlugins(project: Project) {
        super.applyPlugins(project)
        with(project) {
            pluginManager.apply(plugin(PluginId.compose))
            pluginManager.apply(plugin(PluginId.composeCompiler))
        }
    }

    override fun LibraryExtension.configure(
        project: Project,
    ) {
        commonConfigureCompose(project)
    }
}

