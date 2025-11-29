package com.tezov.tuucho.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class AbstractLibraryPlugin : AbstractConventionPlugin() {

    companion object {
        private fun optIn() = listOf<String>(

        ).asIterable()

        private fun compilerOption() = listOf(
            "-Xexpect-actual-classes", // Needed by BuildKonfig
        )
    }

    override fun applyPlugins(project: Project) {
        super.applyPlugins(project)
        with(project) {
            pluginManager.apply(plugin(PluginId.androidLibrary))
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
        }
    }

    override fun configure(
        project: Project,
    ) {
        super.configure(project)
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
                allWarningsAsErrors.set(false)
            }
            // Android
            val androidTargets = listOf(androidTarget())
            androidTargets.forEach {
                it.compilerOptions {
                    jvmTarget.set(this@with.jvmTarget())
                }
            }
            // iOS
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
            }
            applyDefaultHierarchyTemplate() //Needed by BuildKonfig
        }
    }

    private fun configureSourceSets(project: Project) = with(project) {
        val buildTypeCapitalized = buildTypeCapitalized()
        val buildType = buildType()
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                androidMain {
                    kotlin.srcDirs(
                        "${project.projectDir.path}/src/androidMain/$buildType",
                        "${project.projectDir.path}/src/androidMain$buildTypeCapitalized/kotlin"
                    )
                }
                if (isMacOs) {
                    iosMain {
                        kotlin.srcDirs(
                            "${project.projectDir.path}/src/iosMain/$buildType",
                            "${project.projectDir.path}/src/iosMain$buildTypeCapitalized/kotlin"
                        )
                    }
                }
                commonMain {
                    kotlin.srcDirs(
                        "${project.projectDir.path}/src/commonMain/$buildType",
                        "${project.projectDir.path}/src/commonMain$buildTypeCapitalized/kotlin"
                    )
                }
            }
        }
    }

}


