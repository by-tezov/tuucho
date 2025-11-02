package com.tezov.tuucho.convention

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

class ApplicationJvmPlugin : AbstractConventionPlugin() {

    override fun applyPlugins(project: Project) {
        super.applyPlugins(project)
        with(project.pluginManager) {
//            apply(plugin(PluginId.koltinJvm))
//            apply(plugin(PluginId.compose))
//            apply(plugin(PluginId.composeCompiler))
        }
    }

    override fun configure(project: Project) {
        super.configure(project)
//        configureJvmToolchain(project)
//        configureCompose(project)
    }

//    private fun configureJvmToolchain(project: Project) = with(project) {
//        extensions.configure(JavaPluginExtension::class.java) {
//            toolchain.languageVersion.set(javaLanguageVersion())
//        }
//    }
//
//    private fun configureCompose(project: Project) = with(project) {
//        extensions.configure(ComposeExtension::class.java) {
//            desktop {
//                application {
//                    mainClass = "com.tezov.tuucho.MainKt"
//                    nativeDistributions {
//                        targetFormats(
//                            org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
//                            org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
//                            org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
//                        )
//                        packageName = namespace()
//                        packageVersion = versionName()
//                    }
//                }
//            }
//        }
//    }
}
