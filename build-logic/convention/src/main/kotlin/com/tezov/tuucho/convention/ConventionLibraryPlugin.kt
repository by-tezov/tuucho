package com.tezov.tuucho.convention

import com.android.build.api.dsl.LibraryExtension
import com.tezov.tuucho.convention.extension.apply
import com.tezov.tuucho.convention.extension.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ConventionLibraryPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply {
                apply(plugin("android.library"))
                apply(plugin("kotlin"))
            }
            configureKotlin()
            extensions.configure<LibraryExtension> {
                configureAndroid(this)
            }
        }
    }
}



