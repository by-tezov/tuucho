package com.tezov.tuucho.convention.project

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

open class LibraryPlainPlugin : AbstractLibraryPlugin() {

    private val Project.shouldConfigureTest
        get() = buildType() == "mock" || buildType() == "dev"

    override fun applyPlugins(project: Project) {
        super.applyPlugins(project)
        with(project) {
            if (shouldConfigureTest) {
                pluginManager.apply("jacoco")
                pluginManager.apply(plugin(PluginId.allOpen))
                pluginManager.apply(plugin(PluginId.mokkery))
            }
        }
    }

    override fun configure(
        project: Project,
    ) {
        super.configure(project)
        with(project) {
            extra["hasAssets"] = true
            if (shouldConfigureTest) {
                configureCoverage(project)
                configureTest(project)
            }
            if (buildType() == "prod") {
                configureApiValidation(project)
            }
        }
    }

    private fun configureCoverage(project: Project) = with(project) {
        extensions.configure(JacocoPluginExtension::class.java) {
            toolVersion = version("jacoco")
        }
        tasks.register("coverageMockTestReport", JacocoReport::class.java) {
            val unitTestTasks = tasks.withType<Test>()
                .filter { it.name.contains("MockUnitTest") }
            dependsOn(unitTestTasks)

            executionData.setFrom(
                unitTestTasks.map {
                    it.extensions
                        .getByType(JacocoTaskExtension::class.java)
                        .destinationFile
                }
            )
            classDirectories.setFrom(
                fileTree("${layout.buildDirectory.get().asFile}/tmp/kotlin-classes/mock") {
                    exclude(
                        "**/R.class",
                        "**/R$*.class",
                        "**/BuildConfig.*",
                        "**/Manifest*.*",
                        "**/*Test*.*"
                    )
                }
            )
            sourceDirectories.setFrom(files("$projectDir/src/commonMain/kotlin"))

            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }
    }

    private fun configureTest(project: Project) = with(project) {
        extensions.configure(AllOpenExtension::class.java) {
            annotation("${namespaceBase()}.core.domain.test._system.OpenForTest")
        }
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                commonTest {
                    dependencies {
                        implementation(kotlin("test"))
                        implementation(library("kotlinx.coroutines.test"))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAbiValidation::class)
    private fun configureApiValidation(project: Project) = with(project) {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            extensions.configure(AbiValidationMultiplatformExtension::class.java) {
                enabled.set(true)
                legacyDump {
                    referenceDumpDir.set(file(".validation/api"))
                }
            }
        }
    }

}


