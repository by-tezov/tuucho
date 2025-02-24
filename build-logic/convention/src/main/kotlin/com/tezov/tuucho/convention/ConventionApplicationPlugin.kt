package com.tezov.tuucho.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.tezov.tuucho.convention.extension.apply
import com.tezov.tuucho.convention.extension.plugin
import com.tezov.tuucho.convention.extension.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ConventionApplicationPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply {
                apply(plugin("android.application"))
                apply(plugin("kotlin"))
                apply(plugin("kotlin.compose"))
            }
            configureKotlin()
            extensions.configure<ApplicationExtension> {
                configureAndroid(this)
            }
        }
    }
}

private fun Project.configureAndroid(
    extension: ApplicationExtension,
) {
    configureAndroid(extension as CommonExtension<*, *, *, *, *, *>)
    extension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = version("kotlin-plugin")
        }

//        packaging {
//            resources {
//                excludes += "/META-INF/{AL2.0,LGPL2.1}"
//                excludes += "/META-INF/LICENSE.md"
//                excludes += "/META-INF/LICENSE-notice.md"
//                excludes += "/META-INF/*.kotlin_module"
//            }
//        }
    }
}
