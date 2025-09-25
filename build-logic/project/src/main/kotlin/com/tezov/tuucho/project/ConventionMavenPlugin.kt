package com.tezov.tuucho.project

import com.tezov.tuucho.project.AbstractConventionPlugin.PluginId
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ConventionMavenPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            if (buildType() == "prod") {
                pluginManager.apply(plugin(PluginId.maven))
                configureMaven(project)
            }
            configureMavenTask(project)
        }
    }

    private fun configureMavenTask(project: Project) = with(project.rootProject) {
        tasks.register("publishProdToMavenLocal", Exec::class.java) {
            group = "maven"
            description = "Publish kmm lib prod to maven local"

            doFirst {
                commandLine(
                    "${project.rootDir}/gradlew",
                    "publishToMavenLocal"
                )
            }
        }
    }

    private fun configureMaven(project: Project) = with(project) {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            androidTarget {
                publishLibraryVariants("prod")
            }
        }
        extensions.configure(MavenPublishBaseExtension::class.java) {
//            publishToMavenCentral()
//            signAllPublications()
            coordinates(domain(), name(), versionName())
            pom {
                name.set("tuucho")
                description.set("KMM rendering engine")
                inceptionYear.set("2025")
                url.set("https://github.com/by-tezov/tuucho")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    url.set("https://github.com/by-tezov/tuucho")
                    connection.set("scm:git:git://github.com/by-tezov/tuucho.git")
                    developerConnection.set("scm:git:ssh://git@github.com/by-tezov/tuucho.git.git")
                }
                developers {
                    developer {
                        id.set("tezov")
                        name.set("tezov")
                    }
                }
            }
        }
    }
}


