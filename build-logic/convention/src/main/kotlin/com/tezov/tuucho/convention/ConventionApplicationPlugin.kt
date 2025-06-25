package com.tezov.tuucho.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

open class ConventionApplicationPlugin : ConventionPlugin() {

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin("android.application"))
            pluginManager.apply(plugin("kotlin"))
            pluginManager.apply(plugin("kotlin.compose"))
        }
    }

    override fun configureAndroid(
        project: Project,
    ) {
        project.extensions.configure<ApplicationExtension> {
            commonConfigureCompose(project)
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
}


