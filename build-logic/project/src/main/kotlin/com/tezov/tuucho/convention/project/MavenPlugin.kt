package com.tezov.tuucho.convention.project

import com.tezov.tuucho.convention.project.AbstractConventionPlugin.PluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
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
        group = domain()
        version = versionName
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            androidTarget {
                publishLibraryVariants("prod")
            }
        }
        extensions.configure(PublishingExtension::class.java) {
            repositories {
                if (isCI()) {
                    maven {
                        val username = System.getenv("MAVEN_USER_ID")
                        val password = System.getenv("MAVEN_PASSWORD")
                        if(username != null && password != null) {
//                            name = "mavenCentral"
//                            url = uri(
//                                if (isSnapshot())
//                                    "https://central.sonatype.com/repository/maven-snapshots/"
//                                else
//                                    "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
//                            )
                            credentials {
                                this.username = username
                                this.password = password
                            }
                        }
                    }
                }
                maven {
                    name = "projectMaven"
                    url = uri("${rootProject.projectDir}/.m2")
                }
            }
            publications {
                (publications.getByName("kotlinMultiplatform") as MavenPublication).apply {
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
                }
            }
        }
        afterEvaluate {
            extensions.configure(PublishingExtension::class.java) {
                publications.withType(MavenPublication::class.java).configureEach {
                    val artifactId = namespace().removePrefix("${domain()}.")
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
        if (isCI()) {
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
        }
    }
}


