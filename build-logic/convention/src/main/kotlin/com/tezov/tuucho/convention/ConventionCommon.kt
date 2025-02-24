package com.tezov.tuucho.convention

import com.android.build.api.dsl.CommonExtension
import com.tezov.tuucho.convention.extension.javaVersion
import com.tezov.tuucho.convention.extension.jvmTarget
import com.tezov.tuucho.convention.extension.version
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(this@configureKotlin.jvmTarget())
            allWarningsAsErrors.set(false)
            freeCompilerArgs.apply{
                add("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
                add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
            }
        }
    }
}

internal fun Project.configureAndroid(
    extension: CommonExtension<*, *, *, *, *, *>,
) {
    extension.apply {
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