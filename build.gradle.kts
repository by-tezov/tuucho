// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    base
//    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.mokkery) apply false
    alias(libs.plugins.kover) apply true
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.sql.delight) apply false

}

tasks.register<TestReport>("allUnitTestsDebug") {
    group = "verification"
    description = "Aggregates Html unit test reports from all modules into root build folder"
    destinationDirectory.set(layout.buildDirectory.dir("reports/unit-tests"))
    val debugUnitTestTasks = subprojects.flatMap { sub ->
        sub.tasks.withType<Test>().matching { it.name.contains("DebugUnitTest") }
    }
    dependsOn(debugUnitTestTasks)
    testResults.from(debugUnitTestTasks.map { it.binaryResultsDirectory })
}

dependencies {
    subprojects.forEach { sub ->
        if (sub.path == ":app:android") return@forEach
        if (sub.path == ":app:ios") return@forEach
        val buildFile = sub.file("build.gradle.kts")
        if (buildFile.exists()) {
            kover(sub)
        }
    }
}


