plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.all.open)
    compileOnly(libs.maven)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val packageName = "com.tezov.tuucho.project"
group = packageName

gradlePlugin {
    plugins {
        register("ConventionApplicationAndroidPlugin") {
            id = "${packageName}.application-android"
            implementationClass = "${packageName}.${name}"
        }
        register("ConventionApplicationIosPlugin") {
            id = "${packageName}.application-ios"
            implementationClass = "${packageName}.${name}"
        }
        register("ConventionMavenPlugin") {
            id = "${packageName}.maven"
            implementationClass = "${packageName}.${name}"
        }
        register("ConventionLibraryTestPlugin") {
            id = "${packageName}.library-test"
            implementationClass = "${packageName}.${name}"
        }
        register("ConventionLibraryPlainPlugin") {
            id = "${packageName}.library-plain"
            implementationClass = "${packageName}.${name}"
        }
        register("ConventionLibraryUiPlugin") {
            id = "${packageName}.library-ui"
            implementationClass = "${packageName}.${name}"
        }
    }
}

val generateProjectBuildConfig by tasks.registering {
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
            Regex("""^publish(.+)ToMavenLocal$""")
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

        val file = outputDir.file("com/tezov/tuucho/project/BuildConfig.kt").asFile
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
    dependsOn(generateProjectBuildConfig)
}
sourceSets["main"].kotlin.srcDir(generateProjectBuildConfig.map { it.outputs.files })