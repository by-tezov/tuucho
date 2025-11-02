pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven {
            name = "projectMaven"
            url = uri("${rootDir}/../.m2")
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
            url = uri("${rootDir}/../.m2")
        }
        mavenCentral()
        google()
    }
}

//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "tuucho-sample"
include(":app:android")
include(":app:jvm")
val isMacOs = System.getProperty("os.name")
    .startsWith("Mac", ignoreCase = true)
if (isMacOs) {
    include(":app:ios")
}
include(":app:shared")
