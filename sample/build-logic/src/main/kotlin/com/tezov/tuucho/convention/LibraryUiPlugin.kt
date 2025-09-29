package com.tezov.tuucho.convention

import org.gradle.api.Project

class LibraryUiPlugin : LibraryPlainPlugin() {

    override fun applyPlugins(project: Project) {
        super.applyPlugins(project)
        with(project) {
            pluginManager.apply(plugin(PluginId.compose))
            pluginManager.apply(plugin(PluginId.composeCompiler))
        }
    }
}

