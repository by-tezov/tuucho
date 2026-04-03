package com.tezov.tuucho.convention.project

import com.tezov.tuucho.convention.project._system.LibraryId
import com.tezov.tuucho.convention.project._system.PluginId
import com.tezov.tuucho.convention.project._system.buildType
import com.tezov.tuucho.convention.project._system.compilerOption
import com.tezov.tuucho.convention.project._system.javaVersionInt
import com.tezov.tuucho.convention.project._system.library
import com.tezov.tuucho.convention.project._system.optIn
import com.tezov.tuucho.convention.project._system.plugin
import kotlinx.benchmark.gradle.BenchmarksExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

open class BenchmarkPlugin : Plugin<Project> {

    final override fun apply(project: Project) {
        applyPlugins(project)
        configure(project)
    }

    protected open fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
            pluginManager.apply(plugin(PluginId.allOpen))
            pluginManager.apply(plugin(PluginId.kotlinBenchmark))
        }
    }

    protected open fun configure(project: Project) {
        with(project) {
            configureTarget()
            configureMultiplatform()
            configureSourceSets()
            configureKotlinBenchmark()
        }
    }

    private fun Project.configureTarget() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvm()
        }
    }

    private fun Project.configureMultiplatform() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvmToolchain(this@configureMultiplatform.javaVersionInt())
            compilerOptions {
                optIn.addAll(optIn())
                freeCompilerArgs.addAll(compilerOption())
            }
            applyDefaultHierarchyTemplate()
        }
    }

    private fun Project.configureSourceSets() {
        val buildType = buildType()
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                commonMain {
                    kotlin.srcDirs(
                        "${project.projectDir.path}/src/commonMain/$buildType"
                    )
                }
            }
        }
    }

    private fun Project.configureKotlinBenchmark() {
        extensions.configure(AllOpenExtension::class.java) {
            annotation("org.openjdk.jmh.annotations.State")
        }
        extensions.configure(BenchmarksExtension::class.java) {
            targets {
                register("jvm")
            }
        }
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                commonMain.dependencies {
                    implementation(library(LibraryId.kotlinBenchmarkRuntime))
                }
            }
        }
        extensions.configure(BenchmarksExtension::class.java) {
            configurations {
                register("smoke") {
                    param("size", "4", "17")
                    warmups = 3
                    iterations = 5
                    iterationTime = 10
                    iterationTimeUnit = "ms"
                }
                named("main") {
                    param("size", "8", "12")
                    reportFormat = "json"
                }
            }
        }
    }
}
