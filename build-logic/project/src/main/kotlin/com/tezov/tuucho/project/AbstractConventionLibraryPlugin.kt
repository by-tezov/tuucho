package com.tezov.tuucho.project

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class AbstractConventionLibraryPlugin : AbstractConventionPlugin() {

    override fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidLibrary))
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
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
                val namespace = extensions.findByType(CommonExtension::class.java)!!.namespace!!
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

    private fun configureSourceSets(project: Project) = with(project) {
        val buildTypeCapitalized = buildTypeCapitalized()
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                androidMain {
                    kotlin.srcDirs("${project.projectDir.path}/src/androidMain$buildTypeCapitalized/kotlin")
                }
                iosMain {
                    kotlin.srcDirs("${project.projectDir.path}/src/iosMain$buildTypeCapitalized/kotlin")
                }
                commonMain {
                    kotlin.srcDirs("${project.projectDir.path}/src/commonMain$buildTypeCapitalized/kotlin")
                }
            }
        }
    }

}


