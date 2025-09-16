package com.tezov.tuucho.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.tezov.tuucho.convention.ConventionPlugin.Constant.domain
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class ConventionPlugin : Plugin<Project> {

    object Constant {
        const val domain = "com.tezov.tuucho"
    }

    object PluginId {
        const val androidApplication = "android.application"
        const val androidLibrary = "android.library"
        const val koltinAndroid = "kotlin.android"
        const val koltinMultiplatform = "kotlin.multiplatform"
        const val compose = "compose"
        const val composeCompiler = "compose.compiler"
        // test
        const val allOpen = "all.open"
        const val mokkery = "mokkery"
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

        internal fun configureApplication(
            project: Project,
        ) = with(project) {
            extensions.configure(ApplicationExtension::class.java) {
                namespace = namespace()

                defaultConfig {
                    applicationId = "${namespace()}.android"
                    targetSdk = version("targetSdk").toInt()
                    versionCode = version("versionCode").toInt()
                    versionName = version("versionName")
                }
            }
            project.extensions.configure(KotlinAndroidProjectExtension::class.java) {
                jvmToolchain(this@with.javaVersionInt())
                compilerOptions.jvmTarget.set(this@with.jvmTarget())
                compilerOptions.optIn.configureOptIn()
                compilerOptions.allWarningsAsErrors.set(true)
            }
        }

        internal fun configureLibraryMultiplatform(project: Project) = with(project) {
            extensions.configure(LibraryExtension::class.java) {
                namespace = namespace()
            }
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                jvmToolchain(this@with.javaVersionInt())

                compilerOptions {
                    optIn.configureOptIn()
                    allWarningsAsErrors.set(true)
//                    freeCompilerArgs.add("-Xexpect-actual-classes")
//                    freeCompilerArgs.add("-Xlint:unchecked")
//                    freeCompilerArgs.add("-Xlint:deprecation")
                }

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
            val flavorCapitalized = version("flavor").replaceFirstChar { it.uppercaseChar() }
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
                sourceSets.commonMain
            }
            extensions.configure(CommonExtension::class.java) {
                sourceSets["main"].assets.srcDirs(
                    "src/commonMain/resources",
                    "src/commonMain$flavorCapitalized/resources",
                )
            }
        }

        internal fun configureCompose(
            project: Project,
        ) = with(project) {
            extensions.configure(CommonExtension::class.java) {
                lint {
                    disable.apply {
                        add("ComposableNaming")
                    }
                }
            }
        }

        internal fun configureCoverage(project: Project) = with(project) {
            if(version("flavor") != "mock") return@with
            extensions.configure(JacocoPluginExtension::class.java) {
                toolVersion = version("jacoco")
            }
            tasks.register("coverageDebugTestReport", JacocoReport::class.java) {
                val debugUnitTestTasks = tasks.withType<Test>()
                    .filter { it.name.contains("DebugUnitTest") }
                dependsOn(debugUnitTestTasks)

                executionData.setFrom(
                    debugUnitTestTasks.map {
                        it.extensions
                            .getByType(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class.java)
                            .destinationFile
                    }
                )
                classDirectories.setFrom(
                    fileTree("$buildDirectory/tmp/kotlin-classes/debug") {
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

        internal fun configureTest(project: Project) = with(project) {
            if(version("flavor") != "mock") return@with
            extensions.configure(AllOpenExtension::class.java) {
                annotation("$domain.core.domain.test._system.OpenForTest")
            }
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                sourceSets {
                    commonTest {
                        dependencies{
                            implementation(kotlin("test"))
                            implementation(library("kotlinx.coroutines.test"))
                        }
                    }
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


