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

group = "com.tezov.tuucho.project"

gradlePlugin {
    plugins{
        register("ConventionApplicationAndroidPlugin") {
            id = "${group}.application-android"
            implementationClass = "${group}.${name}"
        }
        register("ConventionApplicationIosPlugin") {
            id = "${group}.application-ios"
            implementationClass = "${group}.${name}"
        }
        register("ConventionLibraryPlugin") {
            id = "${group}.library"
            implementationClass = "${group}.${name}"
        }
        register("ConventionLibraryPlainPlugin") {
            id = "${group}.library-plain"
            implementationClass = "${group}.${name}"
        }
        register("ConventionLibraryUiPlugin") {
            id = "${group}.library-ui"
            implementationClass = "${group}.${name}"
        }
    }
}