package com.tezov.tuucho.project

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.kotlin.dsl.extra

abstract class ConventionPlugin : Plugin<Project> {

    object PluginId {
        const val androidApplication = "android.application"
        const val androidLibrary = "android.library"
        const val koltinAndroid = "kotlin.android"
        const val koltinMultiplatform = "kotlin.multiplatform"
        const val compose = "compose"
        const val composeCompiler = "compose.compiler"

        // test
        const val allOpen = "all.open"
        const val mokkery = "mokkery"
    }

    companion object {
        internal fun ListProperty<String>.configureOptIn() {
            add("kotlin.uuid.ExperimentalUuidApi")
            add("kotlin.ExperimentalUnsignedTypes")
            add("kotlin.time.ExperimentalTime")
        }

        internal fun configureCompose(
            project: Project,
        ) = with(project) {
            extensions.configure(CommonExtension::class.java) {
                lint {
                    disable.apply {
                        add("ComposableNaming")
                    }
                }
            }
        }
    }

    final override fun apply(project: Project) {
        applyPlugins(project)
        configureAndroidCommon(project)
        configureBuildType(project)
        configure(project)
    }

    protected abstract fun applyPlugins(project: Project)

    protected open fun configure(project: Project) {}

    private fun configureAndroidCommon(
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

    internal fun configureBuildType(
        project: Project,
    ) = with(project) {
        extensions.configure(CommonExtension::class.java) {
            buildTypes {
                create("prod") {
                    initWith(getByName("release"))
                    matchingFallbacks += listOf("release")
                    isMinifyEnabled = true
                }
                create("stage") {
                    initWith(getByName("debug"))
                    matchingFallbacks += listOf("debug")
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

}


