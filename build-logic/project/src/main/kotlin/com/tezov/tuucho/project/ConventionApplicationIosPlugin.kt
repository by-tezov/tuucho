package com.tezov.tuucho.project

import com.tezov.tuucho.project.buildTypeCapitalized
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.io.File
import java.nio.file.Files

class ConventionApplicationIosPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        configureIosTask(project)
        configureIosAsset(project)
    }

    private fun configureIosTask(iosProject: Project) = with(iosProject) {
        rootProject.tasks.named("clean") {
            val cleanIosBuild = tasks.register("cleanIosBuild") {
                delete("./build")
            }
            dependsOn(cleanIosBuild)
        }
    }

    private fun configureIosAsset(iosProject: Project) = with(iosProject) {
        val syncIosAssets = tasks.register("syncIosAssets") {
            doLast {
                val app = resolveIosApp()
                val mergedAssetsDir = mergeAllProjectAssets(iosProject)
                syncAssetsIntoApp(mergedAssetsDir, app)
                mergedAssetsDir.deleteRecursively()
            }
        }
        gradle.rootProject.allprojects.forEach { project ->
            project.tasks.matching { it.name == "syncComposeResourcesForIos" }
                .configureEach {
                    finalizedBy(syncIosAssets.get())
                }
        }
    }

    private fun resolveIosApp(): File {
        val targetBuildDir = System.getenv("TARGET_BUILD_DIR")
            ?: error("TARGET_BUILD_DIR not set")
        val contentsFolderPath = System.getenv("CONTENTS_FOLDER_PATH")
            ?: error("CONTENTS_FOLDER_PATH not set")
        val app = File(targetBuildDir, contentsFolderPath)
        if (!app.exists()) {
            error(">>> ios.app not found at $app")
        }
        return app
    }

    private fun mergeAllProjectAssets(iosProject: Project) = with(iosProject) {
        val buildTypeCapitalized = buildTypeCapitalized()
        val mergedAssetsDir = Files.createTempDirectory("mergedAssets").toFile()
        rootProject.subprojects
            .filter {
                it.extra.has("hasAssets") && it.extra.get("hasAssets") == true }
            .forEach { project ->
                collectProjectAssets(project, buildTypeCapitalized)
                    .forEach { file ->
                        val idx = file.path.indexOf("assets")
                        val relativePath = file.path.substring(idx + "assets".length + 1)
                        val target = File(mergedAssetsDir, relativePath)
                        if (target.exists()) {
                            println("Overwriting asset: $relativePath (from ${project.path})")
                        }
                        target.parentFile.mkdirs()
                        file.copyTo(target, overwrite = true)
                    }
            }
        mergedAssetsDir
    }

    private fun collectProjectAssets(project: Project, flavorCapitalized: String): List<File> {
        val baseDir = project.projectDir
        val dirs = listOf(
            File(baseDir, "src/commonMain/assets"),
            File(baseDir, "src/commonMain$flavorCapitalized/assets")
        )
        return dirs.filter { it.exists() && it.isDirectory }
            .flatMap { dir -> dir.walkTopDown().filter { it.isFile }.toList() }
    }

    private fun syncAssetsIntoApp(mergedAssetsDir: File, app: File) {
        val assetsDirInApp = File(app, "assets")
        assetsDirInApp.mkdirs()
        val pb = ProcessBuilder(
            "rsync", "-a", "--delete",
            "${mergedAssetsDir.absolutePath}/",
            assetsDirInApp.absolutePath
        )
        pb.inheritIO()
        val result = pb.start().waitFor()
        if (result != 0) {
            error("rsync failed syncing assets into $assetsDirInApp")
        }
    }

}