plugins {
    alias(libs.plugins.convention.microbenchmark)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":ui-component.stable"))
            implementation(libs.koin.core)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.couroutine)
        }
    }
}

benchmark {
    configurations {
//        register("smoke") {
//            include("some benchmark classes")
//            warmups = 3
//            iterations = 5
//            iterationTime = 10
//            iterationTimeUnit = "ms"
//        }
        named("main") {
            reportFormat = "json"
        }
    }
}

