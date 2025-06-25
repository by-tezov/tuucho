package com.tezov.tuucho.convention

import org.gradle.api.Project

open class ConventionLibraryPlugin : ConventionPlugin() {

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin("android.library"))
            pluginManager.apply(plugin("kotlin"))
        }
    }

}


