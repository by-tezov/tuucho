package com.tezov.tuucho.convention.project

import com.tezov.tuucho.convention.project._system.LibraryId
import com.tezov.tuucho.convention.project._system.PluginId
import com.tezov.tuucho.convention.project._system.buildType
import com.tezov.tuucho.convention.project._system.library
import com.tezov.tuucho.convention.project._system.namespaceBase
import com.tezov.tuucho.convention.project._system.plugin
import com.tezov.tuucho.convention.project._system.version
import dev.mokkery.gradle.MokkeryGradleExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
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
        get() = buildType() == "debug"

    private val Project.shouldConfigureApiValidation
        get() = buildType() == "release"

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
            if (shouldConfigureTest) {
                configureCoverage()
                configureTest()
            }
            if (shouldConfigureApiValidation) {
                configureApiValidation()
            }
        }
    }

    private fun Project.configureCoverage() {
        extensions.configure(JacocoPluginExtension::class.java) {
            toolVersion = version("jacoco")
        }
        tasks.register("coverageDebugTestReport", JacocoReport::class.java) {
            val unitTestTasks = tasks.withType<Test>()
                .filter { it.name.contains("DebugUnitTest") }
            dependsOn(unitTestTasks)
            executionData.setFrom(
                unitTestTasks.map {
                    it.extensions
                        .getByType(JacocoTaskExtension::class.java)
                        .destinationFile
                }
            )
            classDirectories.setFrom(
                fileTree("${layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug") {
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

    private fun Project.configureTest() {
        extensions.configure(MokkeryGradleExtension::class.java) {
            with(stubs) {
                allowConcreteClassInstantiation.set(true)
                allowClassInheritance.set(true)
            }
        }
        extensions.configure(AllOpenExtension::class.java) {
            annotation("${namespaceBase()}.core.domain.test._system.OpenForTest")
        }
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                commonTest.dependencies {
                    implementation(library(LibraryId.kotlinTest))
                    implementation(library(LibraryId.kotlinCoroutineTest))
                }
            }
        }
    }

    @OptIn(ExperimentalAbiValidation::class)
    private fun Project.configureApiValidation() = with(project) {
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


