package com.tezov.tuucho.convention

import org.gradle.api.Project

open class ConventionLibraryPlugin : ConventionPlugin() {

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidLibrary))
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
        }
    }

    override fun configure(
        project: Project,
    ) {
        configureKotlinMultiplatform(project)
    }

}


