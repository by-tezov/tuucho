package com.tezov.tuucho.convention

import org.gradle.api.Project

open class ConventionLibraryPlugin : ConventionPlugin() {

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidLibrary))
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
            if(version("flavor") == "mock") {
                pluginManager.apply("jacoco")
                pluginManager.apply(plugin(PluginId.allOpen))
                pluginManager.apply(plugin(PluginId.mokkery))
            }
        }
    }

    override fun configure(
        project: Project,
    ) {
        configureLibraryMultiplatform(project)
        configureSourceSetMultiplatform(project)
        configureCoverage(project)
        configureTest(project)
    }

}


