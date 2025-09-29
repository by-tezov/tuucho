package com.tezov.tuucho.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationIosPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        configureCleanTask(project)
    }

    private fun configureCleanTask(iosProject: Project) = with(iosProject) {
        rootProject.tasks.named("clean") {
            val cleanIosBuild = tasks.register("cleanIosBuild") {
                delete("./build")
            }
            dependsOn(cleanIosBuild)
        }
    }

}