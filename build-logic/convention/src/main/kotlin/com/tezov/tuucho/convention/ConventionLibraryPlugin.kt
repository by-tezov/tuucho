package com.tezov.tuucho.convention

import org.gradle.api.Project

open class ConventionLibraryPlugin : ConventionPlugin() {

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidLibrary))
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
            pluginManager.apply(plugin(PluginId.kover))
            if(version("flavor") == "mock") {
                pluginManager.apply(plugin(PluginId.mokkery))
            }
        }
    }

    override fun configure(
        project: Project,
    ) {
        configureLibraryMultiplatform(project)
        configureSourceSetMultiplatform(project)
        configureKover(project)
        configureTest(project)
    }

}


