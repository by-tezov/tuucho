package com.tezov.tuucho.convention.project

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class AbstractLibraryPlugin : AbstractConventionPlugin() {

    companion object {
        private fun optIn() = listOf(
            "kotlin.uuid.ExperimentalUuidApi",
            "kotlin.ExperimentalUnsignedTypes",
            "kotlin.time.ExperimentalTime",
//            "kotlin.ExperimentalMultiplatform",
        ).asIterable()

        private fun compilerOption() = listOf<String>(

        )
    }

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidLibrary))
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
            pluginManager.apply(plugin(PluginId.conventionMaven))
        }
    }

    override fun configure(
        project: Project,
    ) {
        configureProguard(project)
        configureMultiplatform(project)
        configureSourceSets(project)
    }

    private fun configureProguard(project: Project) = with(project) {
        extensions.configure(LibraryExtension::class.java) {
            buildTypes {
                getByName("prod") {
                    consumerProguardFiles(
                        "proguard-rules.pro"
                    )
                }
            }
        }
    }

    private fun configureMultiplatform(project: Project) = with(project) {
        extensions.configure(LibraryExtension::class.java) {
            namespace = namespace()
        }
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvmToolchain(this@with.javaVersionInt())
            compilerOptions {
                optIn.addAll(optIn())
                freeCompilerArgs.addAll(compilerOption())
                allWarningsAsErrors.set(false) //turn of warning error unique name when maven publication, TODO need to dig in.
            }
            // Android
            val androidTargets = listOf(androidTarget())
            androidTargets.forEach {
                it.compilerOptions {
                    jvmTarget.set(this@with.jvmTarget())
                }
            }
            // iOS
            val isMacOs = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)
            if (isMacOs) {
                val iosTargets = listOf(iosArm64(), iosSimulatorArm64(), iosX64())
                project.afterEvaluate {
                    val namespace = extensions.findByType(CommonExtension::class.java)!!.namespace!!
                    val frameworkName = project.path.split(":")
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
                applyDefaultHierarchyTemplate()
            }
            else {
                println("⚠️ mac os target disable")
            }
        }
    }

    private fun configureSourceSets(project: Project) = with(project) {
        val buildTypeCapitalized = buildTypeCapitalized()
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                androidMain {
                    kotlin.srcDirs("${project.projectDir.path}/src/androidMain$buildTypeCapitalized/kotlin")
                }
                val isMacOs = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)
                if (isMacOs) {
                    iosMain {
                        kotlin.srcDirs("${project.projectDir.path}/src/iosMain$buildTypeCapitalized/kotlin")
                    }
                }
                commonMain {
                    kotlin.srcDirs("${project.projectDir.path}/src/commonMain$buildTypeCapitalized/kotlin")
                }
            }
        }
    }

}


