package com.tezov.tuucho.convention.project

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class AbstractLibraryPlugin : Plugin<Project> {


    object PluginId {
        const val maven = "maven"
        const val signing = "signing"
        const val androidLibrary = "android.library"
        const val koltinMultiplatform = "kotlin.multiplatform"
        const val compose = "compose"
        const val composeCompiler = "compose.compiler"

        // test
        const val allOpen = "all.open"
        const val mokkery = "mokkery"

        // convention
        const val conventionMaven = "convention.maven"
    }

    companion object {

        private fun lintDisabled() = setOf(
            "ComposableNaming"
        )

        private fun optIn() = listOf(
            "kotlin.uuid.ExperimentalUuidApi",
            "kotlin.ExperimentalUnsignedTypes",
            "kotlin.time.ExperimentalTime",
//            "kotlin.ExperimentalMultiplatform",
        ).asIterable()

        private fun compilerOption() = listOf<String>(

        )
    }

    final override fun apply(project: Project) {
        applyPlugins(project)

        configure(project)
    }

    protected open fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.androidLibrary))
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
            pluginManager.apply(plugin(PluginId.conventionMaven))
        }
    }

    protected open fun configure(project: Project) {
        configureCommonAndroid(project)
        configureBuildType(project)
        configureLint(project)
        configureProguard(project)
        configureMultiplatform(project)
        configureSourceSets(project)

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
            } else {
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


