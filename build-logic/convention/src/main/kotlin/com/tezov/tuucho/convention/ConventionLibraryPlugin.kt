package com.tezov.tuucho.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project

open class ConventionLibraryPlugin : ConventionPlugin<LibraryExtension>(LibraryExtension::class) {

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidLibrary))
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
        }
    }

    override fun LibraryExtension.configure(
        project: Project,
    ) {
        configureKotlinMultiplatform(project)
    }

}


