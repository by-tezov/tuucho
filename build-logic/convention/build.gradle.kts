plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.kover.gradle.plugin)
//    compileOnly(libs.mokkery)
    compileOnly(libs.all.open)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "com.tezov.tuucho.convention"

gradlePlugin {
    plugins{
        register("ConventionApplicationPlugin") {
            id = "${group}.application"
            implementationClass = "${group}.${name}"
        }
        register("ConventionLibraryPlugin") {
            id = "${group}.library"
            implementationClass = "${group}.${name}"
        }
        register("ConventionLibraryUiPlugin") {
            id = "${group}.library-ui"
            implementationClass = "${group}.${name}"
        }
    }
}