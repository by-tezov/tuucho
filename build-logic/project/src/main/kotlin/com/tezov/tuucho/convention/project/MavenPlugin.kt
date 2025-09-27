package com.tezov.tuucho.convention.project

import com.tezov.tuucho.convention.project.AbstractConventionPlugin.PluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MavenPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            if (buildType() == "prod") {
                pluginManager.apply(plugin(PluginId.maven))
                pluginManager.apply(plugin(PluginId.signing))
                configureMaven(project)
            }
        }
    }

    private fun configureMaven(project: Project) = with(project) {
        group = domain()
        version = versionName()
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            androidTarget {
                publishLibraryVariants("prod")
            }
        }
        extensions.configure(PublishingExtension::class.java) {
            repositories {
                mavenLocal()
//                maven {
//                    name = "mavenCentral"
//                    url = uri("https://repo.maven.apache.org/maven2/")
//                }
            }
            publications {
                (publications.getByName("kotlinMultiplatform") as MavenPublication).apply {
                    groupId = domain()
                    version = versionName()
                    pom {
                        name.set("tuucho")
                        description.set("KMM rendering engine")
                        url.set("https://github.com/by-tezov/tuucho")
                        inceptionYear.set("2025")
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
                                email.set("tezov.app@gmail.com")
                            }
                        }
                    }
                }
            }
        }
        afterEvaluate {
            extensions.configure(PublishingExtension::class.java) {
                publications.withType(MavenPublication::class.java).configureEach {
                    val artifactId = namespace().removePrefix("${domain()}.")
                    when (this.name) {
                        "kotlinMultiplatform" -> this.artifactId = artifactId
                        "androidProd" -> this.artifactId = "$artifactId-android"
                        "iosProd" -> this.artifactId = "$artifactId-ios"
                    }
                }
            }
        }
//        extensions.configure(SigningExtension::class.java) {
//            sign(extensions.getByType(PublishingExtension::class.java).publications)
//        }
    }
}


