package com.tezov.tuucho.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project

open class ConventionApplicationPlugin : ConventionPlugin<ApplicationExtension>(ApplicationExtension::class) {

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidApplication))
            pluginManager.apply(plugin(PluginId.koltinAndroid))
            pluginManager.apply(plugin(PluginId.compose))
            pluginManager.apply(plugin(PluginId.composeCompiler))
        }
    }

    override fun ApplicationExtension.configure(
        project: Project,
    ) {
        configureCompose(project)
//            packaging {
//                resources {
//                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
//                    excludes += "/META-INF/LICENSE.md"
//                    excludes += "/META-INF/LICENSE-notice.md"
//                    excludes += "/META-INF/*.kotlin_module"
//                }
//            }
    }
}


