package com.tezov.tuucho.convention.project

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal val Project.buildDirectory
    get() = layout.buildDirectory.get().asFile

internal val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.plugin(name: String) =
    libs.findPlugin(name).get().get().pluginId

internal fun Project.library(name: String) =
    libs.findLibrary(name).get()

internal fun Project.bundle(name: String) =
    libs.findBundle(name).get()

internal fun Project.version(name: String) = libs.findVersion(name)
    .get().requiredVersion

internal fun Project.domain() = version("domain")

internal fun Project.name() = version("name")

internal fun Project.namespaceBase() = "${version("domain")}.${version("name")}"

internal fun Project.namespace() = "${namespaceBase()}${path.replace(":", ".").replace("-", "_")}"

internal fun Project.buildType() = BuildConfig.BUILD_TYPE

internal fun Project.buildTypeCapitalized() = buildType().replaceFirstChar { it.uppercaseChar() }

internal fun Project.jvmTarget() = JvmTarget.fromTarget(version("javaVersion"))

internal fun Project.javaVersionInt() = jvmTarget().target.toInt()

internal fun Project.javaVersion() = JavaVersion.toVersion(javaVersionInt())

internal fun Project.javaLanguageVersion() = JavaLanguageVersion.of(javaVersionInt())

internal fun Project.versionName() = version("versionName")


