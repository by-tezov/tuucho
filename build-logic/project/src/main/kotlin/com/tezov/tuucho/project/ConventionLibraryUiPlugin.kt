package com.tezov.tuucho.project

import org.gradle.api.Project

class ConventionLibraryUiPlugin : ConventionLibraryPlainPlugin() {

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
        configureCompose(project)
    }
}

