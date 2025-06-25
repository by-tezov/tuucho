package com.tezov.tuucho.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

abstract class ConventionPlugin : Plugin<Project> {

    companion object {
        internal fun commonConfigureKotlin(project: Project) = with(project) {
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

        internal fun commonConfigureAndroid(
            project: Project,
        ) = with(project) {
            extensions.findByType(CommonExtension::class.java)!!.apply {
                compileSdk = version("compileSdk").toInt()

                defaultConfig {
                    minSdk = version("minSdk").toInt()
                }

                compileOptions {
                    sourceCompatibility = javaVersion()
                    targetCompatibility = javaVersion()
                }
            }
        }

        internal fun commonConfigureCompose(
            project: Project,
        ) = with(project) {
            extensions.findByType(CommonExtension::class.java)!!.apply {
                buildFeatures {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = version("kotlin-plugin")
                }
                lint {
                    disable.add("ComposableNaming")
                }
            }
        }
    }

    final override fun apply(project: Project) {
        applyPlugins(project)
        commonConfigureKotlin(project)
        commonConfigureAndroid(project)
        configureAndroid(project)
    }

    protected abstract fun applyPlugins(project: Project)

    protected open fun configureAndroid(project: Project) {}
}


