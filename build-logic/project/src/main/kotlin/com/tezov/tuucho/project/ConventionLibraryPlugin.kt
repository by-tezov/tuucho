package com.tezov.tuucho.project

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

open class ConventionLibraryPlugin : ConventionPlugin() {

    override fun applyPlugins(project: Project) {

        //TODO here retrieve the current build type

        with(project) {
            pluginManager.apply(plugin(PluginId.androidLibrary))
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
        }
    }

    override fun configure(
        project: Project,
    ) {
        configureLibraryMultiplatform(project)
        configureSourceSetMultiplatform(project)
    }

    private fun configureLibraryMultiplatform(project: Project) = with(project) {
        extensions.configure(LibraryExtension::class.java) {
            namespace = namespace()
        }
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvmToolchain(this@with.javaVersionInt())
            compilerOptions {
                optIn.configureOptIn()
                allWarningsAsErrors.set(true)
            }
            // Android
            val androidTargets = listOf(androidTarget())
            androidTargets.forEach {
                it.compilerOptions {
                    jvmTarget.set(this@with.jvmTarget())
                }
            }
            // iOS
            val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
            project.afterEvaluate {
                val namespace = extensions.findByType(CommonExtension::class.java)!!.namespace
                val baseName = project.path.split(":")
                    .joinToString("") { it.replaceFirstChar { c -> c.uppercaseChar() } } + "Framework"
                iosTargets.forEach { iosTarget ->
                    iosTarget.binaries.framework {
                        isStatic = true
                        this.baseName = baseName
                        freeCompilerArgs += listOf(
                            "-Xbinary=bundleId=$namespace",
                        )
                    }
                }
            }
        }
    }

    private fun configureSourceSetMultiplatform(project: Project) = with(project) {
        val flavorCapitalized = buildTypeCapitalized()
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                androidMain {
                    kotlin.srcDirs("${project.projectDir.path}/src/androidMain$flavorCapitalized/kotlin")
                }
                iosMain {
                    kotlin.srcDirs("${project.projectDir.path}/src/iosMain$flavorCapitalized/kotlin")
                }
                commonMain {
                    kotlin.srcDirs("${project.projectDir.path}/src/commonMain$flavorCapitalized/kotlin")
                }
            }
        }
    }
}


