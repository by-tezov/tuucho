pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "tuucho"
include("sample:android")
include("sample:ios")
listOf("core/data", "core/domain", "core/presentation").forEach { basePath ->
    file(basePath).listFiles()
        ?.filter { it.isDirectory && File(it, "build.gradle.kts").exists() }
        ?.map {
            val relative = it.relativeTo(rootDir).invariantSeparatorsPath
            ":${relative.replace("/", ":")}"
        }?.forEach { include(it) }
}