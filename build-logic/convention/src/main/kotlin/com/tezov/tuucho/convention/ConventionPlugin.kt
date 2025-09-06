package com.tezov.tuucho.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class ConventionPlugin : Plugin<Project> {

    object PluginId {
        const val androidApplication = "android.application"
        const val androidLibrary = "android.library"
        const val koltinAndroid = "kotlin.android"
        const val koltinMultiplatform = "kotlin.multiplatform"
        const val kover = "kover"
        const val mokkery = "mokkery"
        const val compose = "compose"
        const val composeCompiler = "compose.compiler"
    }

    companion object {
        internal fun ListProperty<String>.configureOptIn() {
            add("kotlin.uuid.ExperimentalUuidApi")
            add("kotlin.ExperimentalUnsignedTypes")
            add("kotlin.time.ExperimentalTime")
        }

        internal fun configureAndroidCommon(
            project: Project,
        ) = with(project) {
            extensions.findByType(CommonExtension::class.java)!!.apply {
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
        }

        internal fun configureApplication(
            project: Project,
        ) = with(project) {
            extensions.findByType(ApplicationExtension::class.java)!!.apply {
                defaultConfig {
                    targetSdk = version("targetSdk").toInt()
                    versionCode = version("versionCode").toInt()
                    versionName = version("versionName")
                }
            }
            project.extensions.findByType(KotlinAndroidProjectExtension::class.java)!!.apply {
                jvmToolchain(this@with.javaVersionInt())
                compilerOptions.optIn.configureOptIn()
                compilerOptions.allWarningsAsErrors.set(true)
            }
        }

        internal fun configureLibraryMultiplatform(project: Project) = with(project) {
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                compilerOptions.optIn.configureOptIn()
                compilerOptions.allWarningsAsErrors.set(true)

                val androidTargets = listOf(androidTarget())
                androidTargets.forEach {
                    it.compilerOptions {
                        jvmTarget.set(this@with.jvmTarget())
                    }
                }

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

        internal fun configureSourceSetMultiplatform(project: Project) = with(project) {
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                val flavor = version("flavor").replaceFirstChar { it.uppercaseChar() }
                sourceSets {
                    androidMain {
                        kotlin.srcDirs("${project.projectDir.path}/src/${androidMain.name}$flavor")
                    }
                    iosMain {
                        kotlin.srcDirs("${project.projectDir.path}/src/${iosMain.name}$flavor")
                    }
                    commonMain {
                        kotlin.srcDirs("${project.projectDir.path}/src/${commonMain.name}$flavor")
                    }
                }
            }
        }

        internal fun configureCompose(
            project: Project,
        ) = with(project) {
            extensions.findByType(CommonExtension::class.java)!!.apply {
                lint {
                    disable.apply {
                        add("ComposableNaming")
                    }
                }
            }
        }

        internal fun configureKover(project: Project) = with(project) {
            extensions.configure(KoverProjectExtension::class.java) {
                reports { verify { rule { } } }
            }
            tasks.named("koverHtmlReport") {
                dependsOn.clear()
                val debugTest = tasks.findByName("debugUnitTest")
                if (debugTest != null) {
                    dependsOn(debugTest)
                }
            }
        }
    }

    final override fun apply(project: Project) {
        applyPlugins(project)
        configureAndroidCommon(project)
        configure(project)
    }

    protected abstract fun applyPlugins(project: Project)

    protected open fun configure(project: Project) {}
}


