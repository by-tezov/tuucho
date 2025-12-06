package com.tezov.tuucho.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get

abstract class AbstractConventionPlugin : Plugin<Project> {

    object PluginId {
        const val androidApplication = "android.application"
        const val androidLibrary = "android.library"
        const val koltinAndroid = "kotlin.android"
        const val koltinMultiplatform = "kotlin.multiplatform"
        const val compose = "compose"
        const val composeCompiler = "compose.compiler"
    }

    final override fun apply(project: Project) {
        applyPlugins(project)
        configure(project)
    }

    protected open fun applyPlugins(project: Project) {}

    protected open fun configure(project: Project) {
        with(project) {
            configureCommonAndroid()
            configureBuildType()
            configureAndroidAssets()
        }
    }

    private fun Project.configureCommonAndroid() {
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

    private fun Project.configureBuildType() {
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

    private fun Project.configureAndroidAssets() {
        val buildType = buildType()
        gradle.afterProject {
            if (extra.has("hasAssets") && extra.get("hasAssets") == true) {
                extensions.configure(CommonExtension::class.java) {
                    sourceSets["main"].assets.srcDirs(
                        "src/commonMain/assets",
                        "src/commonMain/$buildType/assets",
                    )
                }
            }
        }
    }

}


