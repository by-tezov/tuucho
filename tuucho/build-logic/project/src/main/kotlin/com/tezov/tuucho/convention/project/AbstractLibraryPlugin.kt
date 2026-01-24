package com.tezov.tuucho.convention.project

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask

abstract class AbstractLibraryPlugin : Plugin<Project> {

    object PluginId {
        const val maven = "maven"
        const val signing = "signing"
        const val androidLibrary = "android.library"
        const val koltinMultiplatform = "kotlin.multiplatform"
        const val compose = "compose"
        const val composeCompiler = "compose.compiler"
        const val ktLint = "ktlint"
        const val detekt = "detekt"

        // test
        const val allOpen = "all.open"
        const val mokkery = "mokkery"

        // convention
        const val conventionMaven = "convention.maven"
    }

    companion object {

        private fun lintDisabled() = setOf<String>(
//            "ComposableNaming"
        )

        private fun optIn() = listOf(
            "kotlin.uuid.ExperimentalUuidApi",
            "kotlin.ExperimentalUnsignedTypes",
            "kotlin.time.ExperimentalTime",
            "kotlin.concurrent.atomics.ExperimentalAtomicApi",
//            "kotlin.ExperimentalMultiplatform",
        ).asIterable()

        private fun compilerOption() = listOf<String>(
//            "-XXLanguage:+ContextParameters"
//            "-Xnested-type-aliases"
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
            pluginManager.apply(plugin(PluginId.ktLint))
            pluginManager.apply(plugin(PluginId.detekt))
            pluginManager.apply(plugin(PluginId.conventionMaven))
        }
    }

    protected open fun configure(project: Project) {
        with(project) {
            configureCommonAndroid()
            configureLint()
            configureKtLint()
            configureDetekt()
            configureProguard()
            configureMultiplatform()
            configureSourceSets()
        }
    }

    private fun Project.configureCommonAndroid() {
        extensions.configure(CommonExtension::class.java) {
            compileSdk = version("compileSdk").toInt()

            buildFeatures {
                //buildConfig = true
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

    private fun Project.configureLint() {
        extensions.configure(CommonExtension::class.java) {
            lint {
//                abortOnError = false
//                checkDependencies = true
//                checkReleaseBuilds = false
//
//                xmlReport = true
//                htmlReport = true
//
//                xmlOutput = file("${layout.buildDirectory.get().asFile}/reports/lint/lint-results.xml")
//                htmlOutput = file("${layout.buildDirectory.get().asFile}/reports/lint/lint-results.html")
//                baseline = file("lint-baseline.xml")
                disable.addAll(lintDisabled())
            }
        }
    }

    private fun Project.configureKtLint() {
        extensions.configure(KtlintExtension::class.java) {
            version.set(version("ktlintRules"))
            baseline.set(file(".validation/ktlint/baseline.xml"))
            debug.set(false)
            verbose.set(false)
            outputToConsole.set(true)
            outputColorName.set("RED")
            reporters {
                reporter(ReporterType.PLAIN)
                reporter(ReporterType.CHECKSTYLE)
            }
            ignoreFailures.set(System.getenv("IS_CI") == "true")
        }
        afterEvaluate {
            tasks.withType(KtLintFormatTask::class.java).configureEach {
                val sourceDirs = listOf(
                    "src/commonMain",
                    "src/androidMain",
                    "src/iosMain",
                    "src/commonTest"
                )
                setSource(files(sourceDirs))
            }
        }
    }

    private fun Project.configureDetekt() {
        extensions.configure(DetektExtension::class.java) {
            toolVersion = version("detektRules")
            buildUponDefaultConfig = true
            allRules = false
            config.setFrom("${rootProject.projectDir}/.detecktrules.yml")
            baseline = file("$projectDir/.validation/detekt/baseline.xml")
            ignoreFailures = System.getenv("IS_CI") == "true"
        }
        val sourceDirs = listOf(
            "src/commonMain",
            "src/androidMain",
            "src/iosMain",
            "src/commonTest"
        )
        afterEvaluate {
            tasks.withType(Detekt::class.java).configureEach {
                jvmTarget = javaVersionString()
                setSource(files(sourceDirs))
            }
            tasks.withType(DetektCreateBaselineTask::class.java).configureEach {
                jvmTarget = javaVersionString()
                setSource(files(sourceDirs))
            }
        }
    }

    private fun Project.configureProguard() {
        extensions.configure(LibraryExtension::class.java) {
            buildTypes {
                getByName("release") {
                    consumerProguardFiles(
                        "proguard-rules.pro"
                    )
                }
            }
        }
    }

    private fun Project.configureMultiplatform() {
        extensions.configure(LibraryExtension::class.java) {
            namespace = namespace()
        }
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvmToolchain(this@configureMultiplatform.javaVersionInt())
            compilerOptions {
                optIn.addAll(optIn())
                freeCompilerArgs.addAll(compilerOption())
                allWarningsAsErrors.set(false) //turn of warning error unique name when maven publication, TODO need to dig in to find what is wrong with KLib
            }
            // Android
            val androidTargets = listOf(androidTarget())
            androidTargets.forEach {
                it.compilerOptions {
                    jvmTarget.set(this@configureMultiplatform.jvmTarget())
                }
            }
            // iOS
            if (isMacOs) {
                iosArm64()
                iosSimulatorArm64()
                iosX64()
            } else {
                println("⚠️ mac os target disable")
            }
            applyDefaultHierarchyTemplate()
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
}


