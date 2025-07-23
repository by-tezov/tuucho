package com.tezov.tuucho.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.reflect.KClass
import kotlin.text.set

abstract class ConventionPlugin<T: Any>(
    private val kclass: KClass<T>
) : Plugin<Project> {

    object PluginId {
        const val androidApplication = "android.application"
        const val androidLibrary = "android.library"

        const val koltinAndroid = "kotlin-android"
        const val koltinMultiplatform = "kotlin-multiplatform"

        const val compose = "compose"
        const val composeCompiler = "compose.compiler"
    }

    companion object {
        internal fun configureKotlinAndroid(project: Project) = with(project) {
            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(this@with.jvmTarget())
                    allWarningsAsErrors.set(false)
                    freeCompilerArgs.apply {
                        add("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
                        add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
                    }
                }
            }
        }

        internal fun configureKotlinMultiplatform(project: Project) = with(project) {
            project.plugins.withId(plugin(PluginId.koltinMultiplatform)) {
                project.extensions.configure(KotlinMultiplatformExtension::class.java) {
                    androidTarget {
                        compilerOptions {
                            jvmTarget.set(this@with.jvmTarget())
                        }
                    }
                }
            }
        }

        internal fun configureAndroid(
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

        internal fun configureCompose(
            project: Project,
        ) = with(project) {
            extensions.findByType(CommonExtension::class.java)!!.apply {
                lint {
                    disable.add("ComposableNaming")
                }
            }
        }
    }

    final override fun apply(project: Project) {
        applyPlugins(project)
        configureKotlinAndroid(project)
        configureAndroid(project)
        project.extensions.configure(kclass) {
            configure(project)
        }
    }

    protected abstract fun applyPlugins(project: Project)

    protected open fun T.configure(project: Project) {}
}


