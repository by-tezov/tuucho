import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    base
    id("jacoco")
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.multiplatform.library) apply false
//    alias(libs.plugins.koin) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.sql.delight) apply false

    alias(libs.plugins.mokkery) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.all.open) apply false
}

// KtLintappDir
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
        val xmlReportsByProject = subprojects.mapNotNull { sub ->
            val reportsDir = sub.layout.buildDirectory.dir("reports/ktlint").get().asFile
            val xmlFiles = reportsDir.walkTopDown()
                .filter { it.isFile && it.extension == "xml" }
                .toList()
            if (xmlFiles.isNotEmpty()) sub.path to xmlFiles else null
        }
        if (xmlReportsByProject.isEmpty()) {
            println("No ktlint XML reports found to aggregate.")
            return@doLast
        }
        val rootReportsDir = layout.buildDirectory.dir("reports/ktlint").get().asFile
        rootReportsDir.deleteRecursively()
        rootReportsDir.mkdirs()
        val aggregatedFile = file("${rootReportsDir.path}/ktlint-aggregated.xml")
        aggregatedFile.bufferedWriter().use { writer ->
            writer.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            writer.appendLine("""<checkstyle version="8.0">""")
            xmlReportsByProject.forEach { (projectPath, files) ->
                val contents = files.mapNotNull { file ->
                    val content = file.readText()
                        .replaceFirst("""<\?xml[^>]*>""".toRegex(), "")
                        .replaceFirst("""<checkstyle[^>]*>""".toRegex(), "")
                        .replace("""</checkstyle>""", "")
                        .trim()
                    content.takeIf { it.isNotEmpty() }
                }
                if (contents.isNotEmpty()) {
                    writer.appendLine("  <!-- ************ Project: $projectPath ************ -->")
                    contents.forEach { content ->
                        writer.appendLine(content.prependIndent("  "))
                    }
                }
            }
            writer.appendLine("</checkstyle>")
        }
        println(
            "Aggregated ${xmlReportsByProject.size} ktlint XML reports into ${
                aggregatedFile.relativeTo(rootProject.projectDir)
            }"
        )
    }
}

val cleanKtLintFolder by tasks.registering(Delete::class) {
    group = "validation"
    description = "Delete KtLint validation folders for all subprojects and root"
    val ktlintProjects = subprojects.filter { sub ->
        sub.file(".validation/ktlint").exists()
    }
    delete(ktlintProjects.map { it.file(".validation/ktlint") } + rootProject.file(".validation/ktlint"))
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
        val baselineFilesByProject = subprojects.mapNotNull { sub ->
            val baselineFile =
                sub.layout.projectDirectory.dir(".validation/ktlint/baseline.xml").asFile
            if (baselineFile.exists()) sub.path to baselineFile else null
        }
        if (baselineFilesByProject.isEmpty()) {
            println("No baseline.xml files found in subprojects.")
            return@doLast
        }
        val rootKtLintDir = layout.projectDirectory.dir(".validation/ktlint").asFile
        rootKtLintDir.deleteRecursively()
        rootKtLintDir.mkdirs()
        val aggregatedFile = file("${rootKtLintDir.path}/baseline-aggregated.xml")
        aggregatedFile.bufferedWriter().use { writer ->
            writer.appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            writer.appendLine("<baseline>")
            baselineFilesByProject.forEach { (projectPath, file) ->
                val content = file.readText()
                    .replaceFirst("""<\?xml[^>]*>""".toRegex(), "")
                    .replaceFirst("""<baseline[^>]*>""".toRegex(), "")
                    .replace("</baseline>", "")
                    .trim()
                if (content.isNotEmpty()) {
                    writer.appendLine("  <!-- ************ Project: $projectPath ************ -->")
                    writer.appendLine(content.prependIndent("  "))
                }
            }
            writer.appendLine("</baseline>")
        }
        println(
            "Aggregated ${baselineFilesByProject.size} ktlint baselines into ${
                aggregatedFile.relativeTo(rootProject.projectDir)
            }"
        )
    }
}

// Detekt
tasks.register("rootDetektReport") {
    group = "validation"
    description = "Check Detekt"
    val detektTasks = subprojects.flatMap { sub ->
        sub.tasks.withType(Detekt::class.java)
            .matching { it.name == "detekt" }
    }
    dependsOn(detektTasks)
    doLast {
        val rootReportsDir = layout.buildDirectory.dir("reports/detekt").get().asFile
        rootReportsDir.deleteRecursively()
        rootReportsDir.mkdirs()
        // ---------- Aggregate XML ----------
        val xmlReportsByProject = subprojects.mapNotNull { sub ->
            val reportsDir = sub.layout.buildDirectory.dir("reports/detekt").get().asFile
            val xmlFile = reportsDir.walkTopDown()
                .firstOrNull { it.isFile && it.extension == "xml" }
            xmlFile?.let { sub.path to it }
        }
        if (xmlReportsByProject.isNotEmpty()) {
            val aggregatedXml = file("${rootReportsDir.path}/detekt-aggregated.xml")
            aggregatedXml.bufferedWriter().use { writer ->
                writer.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
                writer.appendLine("""<checkstyle version="4.3">""")
                xmlReportsByProject.forEach { (projectPath, file) ->
                    val content = file.readText()
                        .replaceFirst("""<\?xml[^>]*>""".toRegex(), "")
                        .replaceFirst("""<checkstyle[^>]*>""".toRegex(), "")
                        .replace("""</checkstyle>""", "")
                        .trim()
                    if (content.isNotEmpty()) {
                        writer.appendLine("  <!-- ************ Project: $projectPath ************ -->")
                        writer.appendLine(content.prependIndent("    "))
                    }
                }
                writer.appendLine("</checkstyle>")
            }
            println(
                "Aggregated ${xmlReportsByProject.size} Detekt XML reports into ${
                    aggregatedXml.relativeTo(rootProject.projectDir)
                }"
            )
        }
        // ---------- Aggregate HTML ----------
        val htmlReportsByProject = subprojects.mapNotNull { sub ->
            val reportsDir = sub.layout.buildDirectory.dir("reports/detekt").get().asFile
            val htmlFile = reportsDir.walkTopDown()
                .firstOrNull { it.isFile && it.extension == "html" }
            htmlFile?.let { sub.path to it }
        }

        if (htmlReportsByProject.isNotEmpty()) {
            val aggregatedHtml = file("${rootReportsDir.path}/detekt-aggregated.html")
            val firstHtml = htmlReportsByProject.first().second.readText()
            val header = firstHtml.substringBefore("<h2>Metrics")
            val footer = "</body>\n</html>"

            aggregatedHtml.bufferedWriter().use { writer ->
                writer.appendLine(header)
                writer.appendLine("<h1>Aggregated Detekt Report</h1>")
                htmlReportsByProject.forEach { (projectName, file) ->
                    writer.appendLine("<h2>Project: $projectName</h2>")
                    val body = file.readText()
                        .substringAfter("<h2>Metrics")
                        .substringBeforeLast(footer)
                    writer.appendLine(body)
                    writer.appendLine("<hr/>")
                }
                writer.appendLine(footer)
            }
            println(
                "Aggregated ${htmlReportsByProject.size} Detekt HTML reports -> ${
                    aggregatedHtml.relativeTo(rootProject.projectDir)
                }"
            )
        }
    }
}

val cleanDetektFolder by tasks.registering(Delete::class) {
    group = "validation"
    description = "Delete Detekt baseline folders for all subprojects"
    val detektProjects = subprojects.filter { sub ->
        sub.tasks.withType(DetektCreateBaselineTask::class.java)
            .any { it.name == "detektBaseline" }
    }
    delete(detektProjects.map { it.file(".validation/detekt") } + rootProject.file(".validation/detekt"))
}
tasks.register("rootUpdateDetektBaseline") {
    group = "validation"
    description = "update Detekt baseline"
    val detektTasks = subprojects.flatMap { sub ->
        sub.tasks.withType(DetektCreateBaselineTask::class.java)
            .matching { it.name == "detektBaseline" }
    }
    detektTasks.forEach {
        it.dependsOn(cleanDetektFolder)
    }
    dependsOn(detektTasks)
    doLast {
        val baselineFilesByProject = subprojects.mapNotNull { sub ->
            val baselineFile =
                sub.layout.projectDirectory.dir(".validation/detekt/baseline.xml").asFile
            if (baselineFile.exists()) sub.path to baselineFile else null
        }
        if (baselineFilesByProject.isEmpty()) {
            println("No baseline.xml files found in subprojects.")
            return@doLast
        }
        val rootDetektDir = layout.projectDirectory.dir(".validation/detekt").asFile
        rootDetektDir.deleteRecursively()
        rootDetektDir.mkdirs()
        val aggregatedFile = file("${rootDetektDir.path}/baseline-aggregated.xml")
        aggregatedFile.bufferedWriter().use { writer ->
            writer.appendLine("""<?xml version="1.0" ?>""")
            writer.appendLine("<SmellBaseline>")
            writer.appendLine("  <ManuallySuppressedIssues>")
            baselineFilesByProject.forEach { (projectPath, file) ->
                val content = file.readText()
                val suppressed = content
                    .substringAfter("<ManuallySuppressedIssues>", "")
                    .substringBefore("</ManuallySuppressedIssues>", "")
                    .trim()
                if (suppressed.isNotEmpty()) {
                    writer.appendLine("    <!-- ************ Project: $projectPath ************ -->")
                    writer.appendLine("    $suppressed")
                }
            }
            writer.appendLine("  </ManuallySuppressedIssues>")
            writer.appendLine("  <CurrentIssues>")
            baselineFilesByProject.forEach { (projectPath, file) ->
                val content = file.readText()
                val issues = content
                    .substringAfter("<CurrentIssues>", "")
                    .substringBefore("</CurrentIssues>", "")
                    .trim()
                if (issues.isNotEmpty()) {
                    writer.appendLine("    <!-- ************ Project: $projectPath ************ -->")
                    writer.appendLine("    $issues")
                }
            }
            writer.appendLine("  </CurrentIssues>")

            writer.appendLine("</SmellBaseline>")
        }

        println(
            "Aggregated ${baselineFilesByProject.size} Detekt baseline files into ${
                aggregatedFile.relativeTo(rootProject.projectDir)
            }"
        )
    }
}

// Abi Validation
tasks.register("rootValidateReleaseApi") {
    group = "validation"
    description = "Validate tuucho release API"
    val abiTasks = subprojects.flatMap { sub ->
        sub.tasks.matching { it.name.equals("checkLegacyAbi", ignoreCase = true) }
    }
    dependsOn(abiTasks)
}

val cleanApiFolder by tasks.registering(Delete::class) {
    group = "validation"
    description = "Delete API validation folders for all subprojects"
    val apiProjects = subprojects.filter { sub ->
        sub.file(".validation/api").exists()
    }
    delete(apiProjects.map { it.file(".validation/api") } + rootProject.file(".validation/api"))
}
tasks.register("rootUpdateReleaseApi") {
    group = "validation"
    description = "Update tuucho release API"
    val abiTasks = subprojects.flatMap { sub ->
        sub.tasks.matching { it.name.equals("updateLegacyAbi", ignoreCase = true) }
    }
    abiTasks.forEach {
        it.dependsOn(cleanApiFolder)
    }
    dependsOn(abiTasks)
    doLast {
        val apiReportsByProject = subprojects.mapNotNull { sub ->
            val reportsDir = sub.layout.projectDirectory.dir(".validation/api").asFile
            val apiFiles = if (reportsDir.exists()) {
                reportsDir.walkTopDown().filter { it.isFile && it.extension == "api" }.toList()
            } else emptyList()
            if (apiFiles.isEmpty()) null else sub.path to apiFiles
        }
        if (apiReportsByProject.isEmpty()) {
            println("No API reports found to aggregate.")
            return@doLast
        }
        val rootApiDir = layout.projectDirectory.dir(".validation/api").asFile
        rootApiDir.deleteRecursively()
        rootApiDir.mkdirs()
        val aggregatedFile = file("${rootApiDir.path}/api-aggregated.api")
        aggregatedFile.bufferedWriter().use { writer ->
            apiReportsByProject.forEach { (projectPath, files) ->
                val contents = files.mapNotNull { file ->
                    val content = file.readText().trim()
                    content.takeIf { it.isNotEmpty() }
                }
                if (contents.isNotEmpty()) {
                    writer.appendLine("************ Project: $projectPath ************")
                    contents.forEach { content ->
                        writer.appendLine(content)
                        writer.appendLine()
                    }
                    writer.appendLine()
                }
            }
        }
        println(
            "Aggregated ${apiReportsByProject.sumOf { it.second.size }} API files into ${
                aggregatedFile.relativeTo(rootProject.projectDir)
            }"
        )
    }
}

// Unit tests + Coverage
tasks.register<TestReport>("rootDebugUnitTest") {
    group = "verification"
    description =
        "Unit test and Aggregates Html unit test reports from all modules into root build folder"
    destinationDirectory.set(layout.buildDirectory.dir("reports/unit-tests"))
    val unitTestTasks = subprojects.flatMap { sub ->
        sub.tasks.withType<Test>().matching {
            it.name.contains("testAndroidHostTest")
        }
    }
    dependsOn(unitTestTasks)
    testResults.from(unitTestTasks.map { it.binaryResultsDirectory })
}

extensions.configure(JacocoPluginExtension::class.java) {
    toolVersion = libs.versions.jacoco.get()
}

tasks.register<JacocoReport>("rootDebugCoverageReport") {
    if (System.getenv("IS_CI") != "true") {
        dependsOn("rootDebugUnitTest")
    }
    group = "verification"
    description = "Aggregates Html coverage report from all modules into root build folder"

    val reportsList = subprojects
        .filterNot {
            it.path in listOf(":sample:android", ":sample:ios") ||
                    !it.file("build.gradle.kts").exists()
        }
        .mapNotNull { sub ->
            sub.tasks.findByName("coverageDebugTestReport") as? JacocoReport
        }

    executionData.setFrom(reportsList.flatMap { it.executionData.files })
    classDirectories.setFrom(reportsList.flatMap { it.classDirectories.files })
    sourceDirectories.setFrom(reportsList.flatMap { it.sourceDirectories.files })

    doFirst {
        val reportDir = layout.buildDirectory.dir("reports/jacoco/html").get().asFile
        if (reportDir.exists()) {
            delete(reportDir)
        }
    }

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoRootReport.xml"))
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
    }
}

tasks.register("rootDebugCoveragePostProcessReport") {

    doFirst {
        val filterHtml = """
                <div style="padding:10px;">
                    <label for="elementsFilter">Filter:</label>
                    <input type="text" id="elementsFilter" onkeyup="filterElements()" placeholder="enter element name...">
                </div>
                <script>
                    function filterElements() {
                        var input = document.getElementById("elementsFilter");
                        var filter = input.value.toLowerCase();
                        var rows = document.querySelectorAll("table tr");
                        rows.forEach(function(row) {
                            var nameCell = row.querySelector("td a.el_package, td a.el_class");
                            if (nameCell) {
                                row.style.display = nameCell.textContent.toLowerCase().includes(filter) ? "" : "none";
                            }
                        });
                    }
                </script>
            """.trimIndent()
        val darkThemeCss = """
                <style>
                    body {
                        background-color: #0b0c0d;
                        color: #d1d5db;
                        font-family: "Inter", sans-serif;
                        margin: 0;
                        padding: 0;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        background-color: #131416;
                        color: #d1d5db;
                    }
                    th, td {
                        border: 1px solid #2c2c2c !important;
                        padding: 6px 8px;
                    }
                    thead td {
                        background-color: #1f1f21 !important; /* dark header background */
                        color: #d1d5db;
                    }
                    tbody tr:nth-child(even) {
                        background-color: #1a1a1c; /* alternate row */
                    }
                    tbody tr:nth-child(odd) {
                        background-color: #131416;
                    }
                    td {
                        color: #d1d5db;
                    }
                    a {
                        color: #ffffff !important;
                        text-decoration: none;
                    }
                    input, select, textarea {
                        background-color: #1f1f21;
                        color: #d1d5db;
                        border: 1px solid #2c2c2c;
                    }
                    label {
                        color: #d1d5db;
                    }
                </style>
            """.trimIndent()
        val darkCodeCss = """
            <style>
                body {
                    background-color: #d1d5db !important;
                    color: #d1d5db !important;
                }
            </style>
        """.trimIndent()


        val reportDir = layout.buildDirectory.dir("reports/jacoco/html").get().asFile
        reportDir.walkTopDown().filter { it.isFile && it.extension == "html" }.forEach { file ->
            var html = file.readText(Charsets.UTF_8)
            val bodyTagRegex = Regex("<body.*?>")
            val bodyTag = bodyTagRegex.find(html)?.value ?: "<body>"
            val injection = when {
                file.name.endsWith(".kt.html") -> "$bodyTag\n$darkCodeCss"
                file.name == "index.html" -> "$bodyTag\n$darkThemeCss\n$filterHtml"
                else -> "$bodyTag\n$darkThemeCss"
            }

            html = html.replaceFirst(bodyTagRegex, injection)
            file.writeText(html, Charsets.UTF_8)
        }
    }
}

tasks.named("rootDebugCoverageReport") {
    finalizedBy("rootDebugCoveragePostProcessReport")
}

// Maven Publication
val cleanMavenLocalFolder by tasks.registering {
    delete(".m2")
}
tasks.register("rootPublishReleaseToMavenLocal") {
    group = "publishing"
    description = "Publish tuucho release to maven local"

    val publishTasks = subprojects.flatMap { sub ->
        sub.tasks.withType<PublishToMavenRepository>()
            .matching { it.name.endsWith("ToProjectMavenRepository") }
    }
    publishTasks.forEach {
        it.dependsOn(cleanMavenLocalFolder)
    }
    dependsOn(publishTasks)
}

tasks.register("rootAdminUpdate") {
    doLast {
        val tasksToRun = listOf(
            "rootFormatKtLint",
            "rootUpdateKtLintBaseline",
            "rootKtLintReport",
            "rootUpdateDetektBaseline",
            "rootDetektReport",
            "rootUpdateReleaseApi",
            "rootValidateReleaseApi",
            //"rootDebugUnitTest",
            //"rootDebugCoverageReport",
            "rootPublishReleaseToMavenLocal"
        )
        tasksToRun.forEach { taskName ->
            println(taskName)
            val process = ProcessBuilder("./gradlew", taskName)
                .directory(rootProject.projectDir)
                .inheritIO()
                .start()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw GradleException("$taskName failed with exit code $exitCode")
            }
        }
    }
}
