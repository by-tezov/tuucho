pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven {
            name = "projectMaven"
            url = uri("${rootDir}/../tuucho/.m2")
        }
        mavenCentral()
        google()
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
        maven {
            name = "projectMaven"
            url = uri("${rootDir}/../tuucho/.m2")
        }
        mavenCentral()
        google()
    }
}

rootProject.name = "tuucho-sample"
val isMacOs = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)
listOf(
    "app",
    "modules"
).forEach { basePath ->
    file(basePath).listFiles()
        ?.filter {
            (it.isDirectory && file("${it}/build.gradle.kts").exists()) || (it.name == "build.gradle.kts")
        }
        ?.map {
            (if (it.isFile) it.parentFile else it).relativeTo(rootDir).invariantSeparatorsPath
        }
        ?.filter { path ->
            !path.endsWith("ios") || path.endsWith("ios") && isMacOs
        }
        ?.forEach { path ->
            val name = path.replace('/', '.')
            include(":$name")
            project(":$name").apply {
                this.name = name
                projectDir = file(path)
            }
        }
}
