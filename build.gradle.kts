// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    base
    id("jacoco")
    alias(libs.plugins.mokkery) apply false
    alias(libs.plugins.all.open) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.sql.delight) apply false
}

tasks.register<TestReport>("rootMockUnitTest") {
    group = "verification"
    description =
        "Unit test and Aggregates Html unit test reports from all modules into root build folder"
    destinationDirectory.set(layout.buildDirectory.dir("reports/unit-tests"))
    val unitTestTasks = subprojects.flatMap { sub ->
        sub.tasks.withType<Test>().matching {
            it.name.contains("MockUnitTest")
        }
    }
    dependsOn(unitTestTasks)
    testResults.from(unitTestTasks.map { it.binaryResultsDirectory })
}

extensions.configure(JacocoPluginExtension::class.java) {
    toolVersion = libs.versions.jacoco.get()
}

tasks.register<JacocoReport>("rootMockCoverageReport") {
    group = "verification"
    description = "Aggregates Html coverage report from all modules into root build folder"

    val reportsList = subprojects
        .filterNot {
            it.path in listOf(":sample:android", ":sample:ios") ||
                    !it.file("build.gradle.kts").exists()
        }
        .mapNotNull { sub ->
            sub.tasks.findByName("coverageMockTestReport") as? JacocoReport
        }

    executionData.setFrom(reportsList.flatMap { it.executionData.files })
    classDirectories.setFrom(reportsList.flatMap { it.classDirectories.files })
    sourceDirectories.setFrom(reportsList.flatMap { it.sourceDirectories.files })

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoRootReport.xml"))
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
    }
}

tasks.register("rootPublishProdToMavenLocal") {
    group = "publishing"
    description = "Publish tuucho prod to maven local"

    val publishTasks = subprojects.flatMap { sub ->
        sub.tasks.withType<PublishToMavenRepository>()
            .matching { it.name.endsWith("ToProjectMavenRepository") }
    }

    dependsOn(publishTasks)
}