package com.tezov.tuucho.project

import com.android.build.gradle.LibraryExtension
import com.tezov.tuucho.project.AbstractConventionPlugin.PluginId
import com.vanniktech.maven.publish.AndroidMultiVariantLibrary
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.environment
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.util.Properties

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
                val mavenPropertiesFile = rootProject.file(mavenPropertiesFilePath())
                if (!mavenPropertiesFile.exists()) {
                    error("⚠️ No maven.properties found")
                }
                with(Properties()) {
                    load(mavenPropertiesFile.inputStream())

                    val userId = getProperty("userId")
                        ?: error("Missing property: userId in maven.properties")
                    val userPassword = getProperty("userPassword")
                        ?: error("Missing property: userPassword in maven.properties")
                    val keyId = getProperty("keyId")
                        ?: error("Missing property: keyId in maven.properties")
                    val keyPassword = getProperty("keyPassword")
                        ?: error("Missing property: keyPassword in maven.properties")

                    val keyArmorFilePath = getProperty("keyArmorFilePath")
                        ?: error("Missing property: keyArmorFilePath in maven.properties")
                    val keyArmorFile = rootProject.file(keyArmorFilePath)
                    if (!keyArmorFile.exists()) {
                        error("⚠️ No $keyArmorFilePath found")
                    }

                    environment(
                        "ORG_GRADLE_PROJECT_mavenCentralUsername" to userId,
                        "ORG_GRADLE_PROJECT_mavenCentralPassword" to userPassword,
                        "ORG_GRADLE_PROJECT_signingInMemoryKeyId" to keyId,
                        "ORG_GRADLE_PROJECT_signingInMemoryKeyPassword" to keyPassword,
                        "ORG_GRADLE_PROJECT_signingInMemoryKey" to keyArmorFile.readText()
                    )
                }
                commandLine(
                    "${project.rootDir}/gradlew",
                    "publishToMavenLocal",
//                    "publishAndReleaseToMavenCentral",
                    "--no-configuration-cache"
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
            configure(
                AndroidSingleVariantLibrary(
                    variant = "prod",
                    sourcesJar = true,
                    publishJavadocJar = false
                )
            )
            publishToMavenCentral(automaticRelease = false)
            signAllPublications()
            coordinates(groupId = domain(), artifactId = name(), version = versionName())
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
                        email.set("tezov.app@gmail.com")
                    }
                }
            }
        }
    }
}


