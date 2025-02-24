package com.tezov.tuucho.convention.extension

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File

internal val Project.buildDirectory : File
    get() = layout.buildDirectory.get().asFile

internal val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.plugin(name: String) : PluginDependency =
    libs.findPlugin(name).get().get()

internal fun Project.library(name: String) =
    libs.findLibrary(name).get()

internal fun Project.bundle(name: String) =
    libs.findBundle(name).get()

internal fun Project.version(name: String): String = libs.findVersion(name)
    .get()
    .requiredVersion

internal fun Project.javaVersion() = JavaVersion.toVersion(version("javaVersion").toInt())

internal fun Project.jvmTarget() = JvmTarget.fromTarget(version("javaVersion"))