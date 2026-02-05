package com.tezov.tuucho.convention

import com.tezov.tuucho.convention._system.PluginId
import com.tezov.tuucho.convention._system.androidLibrary
import com.tezov.tuucho.convention._system.buildType
import com.tezov.tuucho.convention._system.isMacOs
import com.tezov.tuucho.convention._system.javaVersionInt
import com.tezov.tuucho.convention._system.jvmTarget
import com.tezov.tuucho.convention._system.namespace
import com.tezov.tuucho.convention._system.plugin
import com.tezov.tuucho.convention._system.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class AbstractLibraryPlugin : Plugin<Project> {

    abstract val hasAssets: Boolean

    abstract fun optIn(): Iterable<String>

    abstract fun compilerOption(): List<String>

    override fun apply(project: Project) {
        applyPlugins(project)
        configure(project)
    }

    protected open fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
            pluginManager.apply(plugin(PluginId.koltinMultiplatformLibrary))
            pluginManager.apply(plugin(PluginId.koin))
            pluginManager.apply(plugin(PluginId.compose))
            pluginManager.apply(plugin(PluginId.composeCompiler))
        }
    }

    protected open fun configure(
        project: Project,
    ) {
        project.extra["hasAssets"] = hasAssets
        with(project) {
            configureAndroidLibrary()
            configureMultiplatform()
            configureSourceSets()
            configureProguard()
        }
    }

    private fun Project.configureAndroidLibrary() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            androidLibrary {
                namespace = namespace()
                compileSdk = version("compileSdk").toInt()
                minSdk = version("minSdk").toInt()
                compilerOptions.jvmTarget.set(jvmTarget())
            }
        }
    }

    private fun Project.configureMultiplatform() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvmToolchain(this@configureMultiplatform.javaVersionInt())
            compilerOptions {
                optIn.addAll(optIn())
                freeCompilerArgs.addAll(compilerOption())
//                allWarningsAsErrors.set(false)
            }
            // iOS
            if (isMacOs) {
                val iosTargets = listOf(iosArm64(), iosSimulatorArm64())
                project.afterEvaluate {
                    val namespace = namespace()
                    val frameworkName = project.path.removePrefix(":").split(".")
                        .joinToString("") { it.replaceFirstChar { c -> c.uppercaseChar() } } + "Framework"
                    iosTargets.forEach { iosTarget ->
                        iosTarget.binaries.framework {
                            isStatic = true
                            baseName = frameworkName
                            freeCompilerArgs += listOf(
                                "-Xbinary=bundleId=$namespace",
                            )
                        }
                    }
                }
            }
        }
    }

    private fun Project.configureSourceSets() {
        val buildType = buildType()
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                androidMain {
                    kotlin.srcDirs(
                        "${project.projectDir.path}/src/androidMain/$buildType"
                    )
                }
                if (isMacOs) {
                    iosMain {
                        kotlin.srcDirs(
                            "${project.projectDir.path}/src/iosMain/$buildType"
                        )
                    }
                }
                commonMain {
                    kotlin.srcDirs(
                        "${project.projectDir.path}/src/commonMain/$buildType"
                    )
                }
            }
        }
    }

    private fun Project.configureProguard() {
        @Suppress("UnstableApiUsage")
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            androidLibrary {
                optimization {
                    consumerKeepRules.apply {
                        publish = true
                        file("proguard-rules.pro")
                    }
                }
            }
        }
    }
}

