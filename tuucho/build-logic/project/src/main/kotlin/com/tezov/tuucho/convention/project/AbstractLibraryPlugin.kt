package com.tezov.tuucho.convention.project

import com.tezov.tuucho.convention.project._system.PluginId
import com.tezov.tuucho.convention.project._system.androidLibrary
import com.tezov.tuucho.convention.project._system.buildType
import com.tezov.tuucho.convention.project._system.compilerOption
import com.tezov.tuucho.convention.project._system.isMacOs
import com.tezov.tuucho.convention.project._system.javaVersionInt
import com.tezov.tuucho.convention.project._system.javaVersionString
import com.tezov.tuucho.convention.project._system.jvmTarget
import com.tezov.tuucho.convention.project._system.namespace
import com.tezov.tuucho.convention.project._system.optIn
import com.tezov.tuucho.convention.project._system.plugin
import com.tezov.tuucho.convention.project._system.version
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask

abstract class AbstractLibraryPlugin : Plugin<Project> {

    final override fun apply(project: Project) {
        applyPlugins(project)
        configure(project)
    }

    protected open fun applyPlugins(project: Project) {
        with(project) {
            pluginManager.apply(plugin(PluginId.koltinMultiplatform))
            pluginManager.apply(plugin(PluginId.koltinMultiplatformLibrary))
//            pluginManager.apply(plugin(PluginId.koin))
            pluginManager.apply(plugin(PluginId.ktLint))
            pluginManager.apply(plugin(PluginId.detekt))
            pluginManager.apply(plugin(PluginId.conventionMaven))
        }
    }

    protected open fun configure(project: Project) {
        with(project) {
            configureAndroidLibrary()
            configureMultiplatform()
            configureSourceSets()
            configureProguard()
            configureLint()
            configureKtLint()
            configureDetekt()
        }
    }

    private fun Project.configureAndroidLibrary() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            androidLibrary {
                namespace = namespace()
                compileSdk = version("compileSdk").toInt()
                minSdk = version("minSdk").toInt()
                compilerOptions.jvmTarget.set(jvmTarget())
                withHostTestBuilder {}.configure {
                    enableCoverage = true
                }
            }
        }
    }

    private fun Project.configureMultiplatform() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvmToolchain(this@configureMultiplatform.javaVersionInt())
            compilerOptions {
                optIn.addAll(optIn())
                freeCompilerArgs.addAll(compilerOption())
                //turn of warning error unique name when maven publication, TODO need to dig in to find what is wrong with KLib
                allWarningsAsErrors.set(false)
            }
            // iOS
            if (isMacOs) {
                iosArm64()
                iosSimulatorArm64()
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

    private fun Project.configureLint() {
//        extensions.configure(CommonExtension::class.java) {
//            lint {
////                abortOnError = false
////                checkDependencies = true
////                checkReleaseBuilds = false
////
////                xmlReport = true
////                htmlReport = true
////
////                xmlOutput = file("${layout.buildDirectory.get().asFile}/reports/lint/lint-results.xml")
////                htmlOutput = file("${layout.buildDirectory.get().asFile}/reports/lint/lint-results.html")
////                baseline = file("lint-baseline.xml")
//                disable.addAll(lintDisabled())
//            }
//        }
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


