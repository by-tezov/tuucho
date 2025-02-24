import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
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
    }
}