pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
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
                name = id.replace("/", "-")
                projectDir = file(it)
            }
        }
}
