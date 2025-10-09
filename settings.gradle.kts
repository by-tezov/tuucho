pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }
}

//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "tuucho"
listOf(
    "core-barrel",
    "core-modules/data",
    "core-modules/domain",
    "core-modules/presentation"
).forEach { basePath ->
    file(basePath).listFiles()
        ?.filter {
            (it.isDirectory && file("${it}/build.gradle.kts").exists()) || (it.name == "build.gradle.kts")
        }
        ?.map {
            (if (it.isFile) it.parentFile else it)
                .relativeTo(rootDir).invariantSeparatorsPath

        }?.forEach {
            val id = it.replaceFirst(Regex("^/?core-[^/]+"), "core")
            val projectPath = ":${id.replace("/", ":")}"
            include(projectPath)
            project(projectPath).apply {
                val idx = id.lastIndexOf('/')
                name = if (idx == -1) {
                    id
                } else {
                    val prefix = id.substring(0, idx).replace('/', '.')
                    val name = id.substring(idx + 1)
                    "__${prefix}__$name"
                }
                projectDir = file(it)
            }
        }
}

if(System.getenv()["IS_CI"] != "true" && System.getProperty("os.name").contains("Linux", ignoreCase = true)) {
//    includeBuild("sample")
}