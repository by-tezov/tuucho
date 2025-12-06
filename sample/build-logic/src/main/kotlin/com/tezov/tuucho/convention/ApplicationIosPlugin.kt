package com.tezov.tuucho.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationIosPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            configureCleanTask()
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

}
