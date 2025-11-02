// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    base
    id("jacoco")
    alias(libs.plugins.mokkery) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.all.open) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.sql.delight) apply false
}

tasks.register("rootFormatKtLint") {
    group = "validation"
    description = "Format KtLint"
    val ktLineTasks = subprojects.flatMap { sub ->
        sub.tasks.matching {
            it.name.startsWith(
                "ktlint",
                ignoreCase = true
            ) && it.name.endsWith("Format", ignoreCase = true)
        }
    }
    dependsOn(ktLineTasks)
}

tasks.register("rootKtLintReport") {
    group = "validation"
    description = "Check KtLint"
    val ktLineTasks = subprojects.flatMap { sub ->
        sub.tasks.matching {
            it.name.startsWith(
                "ktlint",
                ignoreCase = true
            ) && it.name.endsWith("Check", ignoreCase = true)
        }
    }
    dependsOn(ktLineTasks)
    doLast {
        val allXmlReports = subprojects.flatMap { sub ->
            val reportsDir = sub.layout.buildDirectory.dir("reports/ktlint").get().asFile
            if (!reportsDir.exists()) return@flatMap emptyList()
            reportsDir.walkTopDown()
                .filter { it.isFile && it.extension == "xml" }
                .toList()
        }
        if (allXmlReports.isEmpty()) {
            println("No ktlint XML reports found to aggregate.")
            return@doLast
        }
        val rootReportsDir = layout.buildDirectory.dir("reports/ktlint").get().asFile
        if (rootReportsDir.exists()) {
            rootReportsDir.deleteRecursively()
        }
        rootReportsDir.mkdirs()
        val aggregatedFile = file("${rootReportsDir.path}/ktlint-aggregated.xml")
        aggregatedFile.bufferedWriter().use { writer ->
            writer.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            writer.appendLine("<checkstyle>")
            allXmlReports.forEach { file ->
                val content = file.readText()
                    .replaceFirst("""<\?xml[^>]*>""".toRegex(), "")      // strip XML headers
                    .replaceFirst("""<checkstyle[^>]*>""".toRegex(), "") // strip opening tag
                    .replace("""</checkstyle>""", "")                    // strip closing tag
                    .trim()
                if (content.isNotEmpty()) writer.appendLine(content)
            }
            writer.appendLine("</checkstyle>")
        }
        println(
            "Aggregated ${allXmlReports.size} ktlint XML reports into ${
                aggregatedFile.relativeTo(
                    rootProject.projectDir
                )
            }"
        )
    }
}

val cleanKtLintFolder by tasks.registering {
    delete(".ktlint")
}
tasks.register("rootUpdateKtLintBaseline") {
    group = "validation"
    description = "update KtLint baseline"
    val ktLineTasks = subprojects.flatMap { sub ->
        sub.tasks.matching { it.name.equals("ktlintGenerateBaseline", ignoreCase = true) }
    }
    ktLineTasks.forEach {
        it.dependsOn(cleanKtLintFolder)
    }
    dependsOn(ktLineTasks)
    doLast {
        val allBaselines = subprojects.mapNotNull { sub ->
            val baselineFile =
                sub.layout.projectDirectory.dir(".validation/ktlint/baseline.xml").asFile
            baselineFile.takeIf { it.exists() }
        }
        if (allBaselines.isEmpty()) {
            println("No baseline.xml files found in subprojects.")
            return@doLast
        }
        val rootKtLintDir = layout.projectDirectory.dir(".validation/ktlint").asFile
        if (rootKtLintDir.exists()) {
            rootKtLintDir.deleteRecursively()
        }
        rootKtLintDir.mkdirs()
        val aggregatedFile = file("${rootKtLintDir.path}/baseline-aggregated.xml")
        aggregatedFile.bufferedWriter().use { writer ->
            writer.appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            writer.appendLine("<baseline>")
            allBaselines.forEach { file ->
                val content = file.readText()
                    .replaceFirst("""<\?xml[^>]*>""".toRegex(), "")
                    .replaceFirst("""<baseline[^>]*>""".toRegex(), "")
                    .replace("</baseline>", "")
                    .trim()
                if (content.isNotEmpty()) {
                    writer.appendLine("<!-- From ${file.relativeTo(rootProject.projectDir)} -->")
                    writer.appendLine(content)
                }
            }
            writer.appendLine("</baseline>")
        }
        println(
            "Aggregated ${allBaselines.size} ktlint baselines into ${
                aggregatedFile.relativeTo(
                    rootProject.projectDir
                )
            }"
        )
    }
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

val cleanMavenLocalFolder by tasks.registering {
    delete(".m2")
}
rootProject.tasks.named("clean") {
    dependsOn(cleanMavenLocalFolder)
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
    publishTasks.forEach {
        it.dependsOn(cleanMavenLocalFolder)
    }
    dependsOn(publishTasks)
}

tasks.register("rootValidateProdApi") {
    group = "validation"
    description = "Validate tuucho prod API"
    val abiTasks = subprojects.flatMap { sub ->
        sub.tasks.matching { it.name.equals("checkLegacyAbi", ignoreCase = true) }
    }
    dependsOn(abiTasks)
}

val cleanApiFolder by tasks.registering {
    delete(".api")
}
tasks.register("rootUpdateProdApi") {
    group = "validation"
    description = "Update tuucho prod API"
    val abiTasks = subprojects.flatMap { sub ->
        sub.tasks.matching { it.name.equals("updateLegacyAbi", ignoreCase = true) }
    }
    abiTasks.forEach {
        it.dependsOn(cleanApiFolder)
    }
    dependsOn(abiTasks)
    doLast {
        val allApiReports = subprojects.flatMap { sub ->
            val reportsDir = sub.layout.projectDirectory.dir(".validation/api").asFile
            if (!reportsDir.exists()) return@flatMap emptyList()
            reportsDir.walkTopDown()
                .filter { it.isFile && it.extension == "api" }
                .toList()
        }
        if (allApiReports.isEmpty()) {
            println("No API reports found to aggregate.")
            return@doLast
        }

        val rootApiDir = layout.projectDirectory.dir(".validation/api").asFile
        if (rootApiDir.exists()) {
            rootApiDir.deleteRecursively()
        }
        rootApiDir.mkdirs()
        val aggregatedFile = file("${rootApiDir.path}/api-aggregated.xml")
        aggregatedFile.bufferedWriter().use { writer ->
            allApiReports.forEach { file ->
                val content = file.readText().trim()
                if (content.isNotEmpty()) {
                    writer.appendLine(content)
                    writer.appendLine() // spacing between files
                }
            }
        }
        println(
            "Aggregated ${allApiReports.size} API files into ${
                aggregatedFile.relativeTo(rootProject.projectDir)
            }"
        )
    }
}
