package com.tezov.tuucho.convention.project._system

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

// Hack because it is missing inside the gradle dependencies
internal fun KotlinMultiplatformExtension.android(block: KotlinMultiplatformAndroidLibraryTarget.() -> Unit) {
    configure<KotlinMultiplatformAndroidLibraryTarget>(block)
}
