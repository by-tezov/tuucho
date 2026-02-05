plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val packageName = "com.tezov.tuucho.convention"
group = packageName

gradlePlugin {
    plugins {
        register("ApplicationAndroidPlugin") {
            id = "${packageName}.application-android"
            implementationClass = "${packageName}.${name}"
        }
        register("ApplicationIosPlugin") {
            id = "${packageName}.application-ios"
            implementationClass = "${packageName}.${name}"
        }
        register("SharedLibraryPlugin") {
            id = "${packageName}.shared-library"
            implementationClass = "${packageName}.${name}"
        }
        register("UiExtensionLibraryPlugin") {
            id = "${packageName}.ui-extension-library"
            implementationClass = "${packageName}.${name}"
        }
    }
}

val generateProjectBuildConfigTask by tasks.registering {
    group = "build setup"
    description = "Generate build config file"

    val outputDir = layout.projectDirectory.dir("src/generated/kotlin")
    outputs.dir(outputDir)
    outputs.upToDateWhen { false }

    fun buildConfigContent(packageName: String, value: String) = """
        package $packageName
    
        object BuildConfig {
            const val BUILD_TYPE: String = "$value"
        }
    """.trimIndent()

    doFirst {
        val rootTasks = gradle.parent?.startParameter?.taskNames.orEmpty()
        val regexes = listOf(
            Regex("""^assemble(.+)$"""),
            Regex("""^root(.+)UnitTest$"""),
            Regex("""^root(.+)CoverageReport$"""),
            Regex("""^publish(.+)ToMavenLocal$"""),
            Regex("""^launch(.+)SimulatorApp$""")
        )

        val buildTypeFound = rootTasks
            .asSequence()
            .map { it.substringAfterLast(":") }
            .firstNotNullOfOrNull { task ->
                regexes.firstNotNullOfOrNull { regex ->
                    regex.matchEntire(task)?.groupValues?.get(1)
                }
            }
            ?.lowercase()
            ?.takeIf { listOf("mock", "dev", "stage", "prod").contains(it) }

        val file = outputDir.file("com/tezov/tuucho/convention/BuildConfig.kt").asFile
        file.parentFile.mkdirs()
        val buildTypeResolved: String? = when {
            !file.exists() -> buildTypeFound ?: "mock".also {
                println("⚠️ buildType not found and BuildConfig didn't exist → create BuildConfig.kt with 'mock' build type")
            }

            buildTypeFound == null -> null.also {
                println("⚠️ buildType not found but BuildConfig exist → keep BuildConfig.kt current build type")
            }

            else -> buildTypeFound
        }
        if (buildTypeResolved != null) {
            file.writeText(buildConfigContent(packageName, buildTypeResolved))
        }
        if (file.exists()) {
            val content = file.readText()
            val match = Regex("""const val BUILD_TYPE: String = "([^"]+)"""").find(content)
            val currentValue = match?.groupValues?.get(1)
            println("Current BuildConfig BUILD_TYPE = $currentValue")
        } else {
            error("BuildConfig.kt not found at ${file.absolutePath}")
        }
    }
}
tasks.named("checkKotlinGradlePluginConfigurationErrors") {
    dependsOn(generateProjectBuildConfigTask)
}
sourceSets["main"].kotlin.srcDir(generateProjectBuildConfigTask.map { it.outputs.files })