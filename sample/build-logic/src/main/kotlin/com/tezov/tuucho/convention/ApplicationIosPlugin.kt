package com.tezov.tuucho.convention

import com.tezov.tuucho.convention._system.AssetHelper
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ApplicationIosPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            configureCleanTask()
            configureAssets()
        }
    }

    private fun Project.configureCleanTask() {
        val cleanIosBuild = tasks.register("cleanIosBuild") {
            delete("./build")
        }
        rootProject.tasks.named("clean") {
            dependsOn(cleanIosBuild)
        }
    }

    private fun Project.configureAssets() {
        AssetHelper.run {
            registerTask(
                taskName = "syncIosAssets",
                appDir = { resolveIosAppDir() },
                attachToTask = "syncComposeResourcesForIos"
            )
        }
    }

    private fun resolveIosAppDir(): File {
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
}
