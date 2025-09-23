plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
//    compileOnly(libs.mokkery)
    compileOnly(libs.all.open)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val packageName = "com.tezov.tuucho.project"
group = packageName

gradlePlugin {
    plugins{
        register("ConventionApplicationAndroidPlugin") {
            id = "${packageName}.application-android"
            implementationClass = "${packageName}.${name}"
        }
        register("ConventionApplicationIosPlugin") {
            id = "${packageName}.application-ios"
            implementationClass = "${packageName}.${name}"
        }
        register("ConventionLibraryPlugin") {
            id = "${packageName}.library"
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
        val buildTypeFound = rootTasks
            .map { it.substringAfterLast(":") }
            .firstOrNull { it.startsWith("assemble", ignoreCase = true) }
            ?.removePrefix("assemble")
            ?.takeIf { it.isNotEmpty() }
            ?.lowercase()

        val file = outputDir.file("com/tezov/tuucho/project/BuildConfig.kt").asFile
        file.parentFile.mkdirs()
        val buildTypeResolved: String? = when {
            !file.exists() -> buildTypeFound ?: "mock".also {
                println("⚠️ buildType not found and BuildConfig didn't exist → create BuildConfig.kt with 'mock' build type")
            }
            buildTypeFound != null -> buildTypeFound
            else -> null
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
            println("BuildConfig.kt not found at ${file.absolutePath}")
        }
    }
}
tasks.named("checkKotlinGradlePluginConfigurationErrors") {
    dependsOn(generateProjectBuildConfig)
}
sourceSets["main"].kotlin.srcDir(generateProjectBuildConfig.map { it.outputs.files })