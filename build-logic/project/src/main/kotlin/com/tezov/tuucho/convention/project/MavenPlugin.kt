package com.tezov.tuucho.convention.project

import com.tezov.tuucho.convention.project.AbstractConventionPlugin.PluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MavenPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            if (buildType() == "prod") {
                pluginManager.apply(plugin(PluginId.maven))
                if (isCI()) {
                    pluginManager.apply(plugin(PluginId.signing))
                }
                configureMaven(project)
            }
        }
    }

    private fun configureMaven(project: Project) = with(project) {
        val versionName = "${versionName()}${if (isSnapshot()) "-SNAPSHOT" else ""}"
        val artifactId = namespace().removePrefix("${domain()}.")

        group = domain()
        version = versionName
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            androidTarget {
                publishLibraryVariants("prod")
            }
        }
        extensions.configure(PublishingExtension::class.java) {
            repositories {
                maven {
                    name = "projectMaven"
                    url = uri("${rootProject.projectDir}/.m2")
                }
            }
        }

        afterEvaluate {
            if (isCI()) {
                tasks.register("placeholderJavadocJar", Jar::class.java) {
                    archiveClassifier.set("javadoc")
                    val readme = layout.buildDirectory.file("generated/javadoc/README.md")
                    doFirst {
                        val file = readme.get().asFile
                        file.parentFile.mkdirs()
                        file.writeText(
                            """
                            Tuucho rendering engine project:
                            - Documentation: https://doc.tuucho.com/latest/
                            - Repository: https://github.com/by-tezov/tuucho
                            - Contact: tezov.app@gmail.com
                        """.trimIndent()
                        )
                    }
                    from(readme)
                }

                extensions.configure(SigningExtension::class.java) {
                    sign(extensions.getByType(PublishingExtension::class.java).publications)
                    val keyArmored = System.getenv("MAVEN_SIGNING_KEY").takeIf {
                        it.isNotBlank()
                    } ?: error("Missing env: MAVEN_SIGNING_KEY")
                    val keyPassword = System.getenv("MAVEN_SIGNING_PASSWORD").takeIf {
                        it.isNotBlank()
                    } ?: error("Missing env: MAVEN_SIGNING_PASSWORD")
                    useInMemoryPgpKeys(keyArmored, keyPassword)
                }

                tasks.withType<PublishToMavenRepository>().configureEach {
                    dependsOn(tasks.withType<Sign>())
                }
            }

            extensions.configure(PublishingExtension::class.java) {
                publications.withType(MavenPublication::class.java).configureEach {
                    tasks.findByName("placeholderJavadocJar")?.let { artifact(it) }

                    groupId = domain()
                    version = versionName
                    pom {
                        name.set("tuucho")
                        description.set("KMM rendering engine")
                        url.set("https://doc.tuucho.com/latest/")
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
                    when (name) {
                        "kotlinMultiplatform" -> this.artifactId = artifactId
                        "androidProd" -> this.artifactId = "$artifactId-android"
                        "iosArm64" -> this.artifactId = "$artifactId-iosArm64"
                        "iosSimulatorArm64" -> this.artifactId = "$artifactId-iosSimulatorArm64"
                        "iosX64" -> this.artifactId = "$artifactId-iosX64"
                    }
                }
            }
        }
    }
}


