package com.tezov.tuucho.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

abstract class AbstractConventionPlugin : Plugin<Project> {

    object PluginId {
        const val androidApplication = "android.application"
        const val androidLibrary = "android.library"
        const val koltinAndroid = "kotlin.android"
        const val koltinMultiplatform = "kotlin.multiplatform"
        const val koltinJvm = "kotlin.jvm"
        const val compose = "compose"
        const val composeCompiler = "compose.compiler"
    }

    companion object {

        private fun lintDisabled() = setOf<String>(
//            "ComposableNaming"
        )

    }

    final override fun apply(project: Project) {
        applyPlugins(project)
        configure(project)
    }

    protected open fun applyPlugins(project: Project) {

    }

    protected open fun configure(project: Project) {
        configureCommonAndroid(project)
        configureBuildType(project)
        configureLint(project)
    }

    private fun configureCommonAndroid(
        project: Project,
    ) = with(project) {
        extensions.configure(CommonExtension::class.java) {
            compileSdk = version("compileSdk").toInt()

            buildFeatures {
                buildConfig = true
            }

            defaultConfig {
                minSdk = version("minSdk").toInt()
            }

            compileOptions {
                sourceCompatibility = javaVersion()
                targetCompatibility = javaVersion()
            }
        }
        project.extensions.configure(JavaPluginExtension::class.java) {
            toolchain {
                languageVersion.set(javaLanguageVersion())
            }
        }
    }

    private fun configureBuildType(
        project: Project,
    ) = with(project) {
        extensions.configure(CommonExtension::class.java) {
            buildTypes {
                create("prod") {
                    initWith(getByName("release"))
                    matchingFallbacks += listOf("release")
                }
                create("stage") {
                    initWith(getByName("release"))
                    matchingFallbacks += listOf("release")
                }
                create("dev") {
                    initWith(getByName("debug"))
                    matchingFallbacks += listOf("debug")
                }
                create("mock") {
                    initWith(getByName("debug"))
                    matchingFallbacks += listOf("debug")
                }
            }
        }
        extensions.configure(AndroidComponentsExtension::class.java) {
            beforeVariants { builder ->
                if (builder.buildType == "debug" || builder.buildType == "release") {
                    builder.enable = false
                }
            }
        }
    }

    private fun configureLint(
        project: Project,
    ) = with(project) {
        extensions.configure(CommonExtension::class.java) {
            lint {
                disable.addAll(lintDisabled())
            }
        }
    }

}


