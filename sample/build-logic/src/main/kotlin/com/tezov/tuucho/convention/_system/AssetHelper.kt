package com.tezov.tuucho.convention._system

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.io.File
import java.nio.file.Files

object AssetHelper {

    fun Project.registerTask(
        taskName: String,
        appDir: () -> File,
        attachToTask: String
    ) {
        val syncAssets = tasks.register(taskName) {
            doLast {
                val mergedAssetsDir = mergeAllProjectAssets()
                syncAssetsIntoApp(mergedAssetsDir, appDir())
                mergedAssetsDir.deleteRecursively()
            }
        }
        gradle.rootProject.allprojects.forEach { subProject ->
            buildType().replaceFirstChar { it.uppercaseChar() }
            subProject.tasks.matching { it.name == attachToTask }
                .configureEach { finalizedBy(syncAssets.get()) }
        }
    }

    private fun Project.mergeAllProjectAssets(): File {
        val buildType = buildType()

        @Suppress("NewApi")
        val mergedAssetsDir = Files.createTempDirectory("mergedAssets").toFile()
        rootProject.subprojects
            .filter {
                it.extra.has("hasAssets") && it.extra.get("hasAssets") == true
            }
            .forEach { subProject ->
                subProject.collectProjectAssets(buildType)
                    .forEach { file ->
                        val idx = file.path.indexOf("assets")
                        val relativePath = file.path.substring(idx + "assets".length + 1)
                        val target = File(mergedAssetsDir, relativePath)
                        if (target.exists()) {
                            println("Overwriting asset: $relativePath (from ${subProject.path})")
                        }
                        target.parentFile.mkdirs()
                        file.copyTo(target, overwrite = true)
                    }
            }
        return mergedAssetsDir
    }

    private fun Project.collectProjectAssets(buildType: String): List<File> {
        val baseDir = projectDir
        val dirs = listOf(
            File(baseDir, "src/commonMain/assets"),
            File(baseDir, "src/commonMain/$buildType/assets")
        )
        return dirs.filter { it.exists() && it.isDirectory }
            .flatMap { dir -> dir.walkTopDown().filter { it.isFile }.toList() }
    }

    private fun syncAssetsIntoApp(mergedAssetsDir: File, appDir: File) {
        val assetsDirInApp = File(appDir, "assets")
        assetsDirInApp.mkdirs()
        val process = ProcessBuilder(
            "rsync", "-a", "--delete",
            "${mergedAssetsDir.absolutePath}/",
            assetsDirInApp.absolutePath
        )
        @Suppress("NewApi")
        process.inheritIO()
        val result = process.start().waitFor()
        if (result != 0) {
            error("rsync failed syncing assets into $assetsDirInApp")
        }
    }
}