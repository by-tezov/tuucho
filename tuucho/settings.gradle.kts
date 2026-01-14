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
    "core-modules/presentation",
    "ui-component-modules/stable",
    "core-modules/umbrella",
).forEach { basePath ->
    file(basePath).listFiles()
        ?.filter {
            (it.isDirectory && file("${it}/build.gradle.kts").exists()) || (it.name == "build.gradle.kts")
        }
        ?.map {
            (if (it.isFile) it.parentFile else it)
                .relativeTo(rootDir).invariantSeparatorsPath

        }?.forEach { path ->
            val segments = path.split("/", limit = 2)
            val firstSegment = when {
                segments[0] == "core-barrel" -> "core"
                segments[0].endsWith("-modules") -> segments[0].removeSuffix("-modules")
                else -> segments[0]
            }
            val id = if (segments.size > 1) "$firstSegment/${segments[1]}" else firstSegment
            val name = id.replace('/', '.')
            include(":$name")
            project(":$name").apply {
                this.name = name
                projectDir = file(path)
            }
        }
}
