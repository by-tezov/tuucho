pluginManagement {
    includeBuild("build-logic")
    repositories {
//        maven {
//            name = "Central Portal Snapshots"
//            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
//            content {
//                includeModule("com.tezov", "tuucho.core")
//            }
//        }
        maven {
            name = "projectMaven"
            url = uri("${rootDir}/../.m2")
        }
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
        maven {
            name = "projectMaven"
            url = uri("${rootDir}/../.m2")
        }
        google()
        mavenCentral()
    }
}

//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "tuucho-sample"
include(":app:android")
val isMacOs = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)
if (isMacOs) {
    include(":app:ios")
}
include(":app:shared")
