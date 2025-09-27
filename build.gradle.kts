import java.util.Properties

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

tasks.register("rootPublishProdToMavenLocal", Exec::class.java) {
    group = "maven"
    description = "Publish kmm lib prod to maven local"

    commandLine(
        "./gradlew",
        "publishToMavenLocal",
//        "publishAndReleaseToMavenCentral",
        "--no-configuration-cache"
    )

    doFirst {
        val mavenPropertiesFile = file("maven.properties")
        if (!mavenPropertiesFile.exists()) {
            error("⚠️ No maven.properties found")
        }
        with(Properties()) {
            load(mavenPropertiesFile.inputStream())

            val userId = getProperty("userId")
                ?: error("Missing property: userId in maven.properties")
            val userPassword = getProperty("userPassword")
                ?: error("Missing property: userPassword in maven.properties")
            val keyId = getProperty("keyId")
                ?: error("Missing property: keyId in maven.properties")
            val keyPassword = getProperty("keyPassword")
                ?: error("Missing property: keyPassword in maven.properties")

            val keyArmorFilePath = getProperty("keyArmorFilePath")
                ?: error("Missing property: keyArmorFilePath in maven.properties")
            val keyArmorFile = file(keyArmorFilePath)
            if (!keyArmorFile.exists()) {
                error("⚠️ No $keyArmorFilePath found")
            }

            environment(
                "ORG_GRADLE_PROJECT_mavenCentralUsername" to userId,
                "ORG_GRADLE_PROJECT_mavenCentralPassword" to userPassword,
                "ORG_GRADLE_PROJECT_signingInMemoryKeyId" to keyId,
                "ORG_GRADLE_PROJECT_signingInMemoryKeyPassword" to keyPassword,
                "ORG_GRADLE_PROJECT_signingInMemoryKey" to keyArmorFile.readText()
            )
        }
    }
}