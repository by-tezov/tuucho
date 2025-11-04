// TODO use when I update all to Java 21
//@file:DependsOn("io.github.bastosss77:ktlint-danger-kotlin:0.5.1") // kotlinc 2.2.20 / Java 21
//
//import io.github.bastosss77.danger.ktlint.KtlintPlugin
//import systems.danger.kotlin.*
//import java.io.File
//
//register.plugin(KtlintPlugin)
//danger(args) {
//   reportWithKtLintPlugin()
//}
//
//fun reportWithKtLintPlugin() {
//   with(KtlintPlugin) {
//      val report = File("build/reports/ktlint/ktlint-aggregated.xml")
//      report(parse(report))
//   }
//}

import systems.danger.kotlin.*
import java.io.File

danger(args) {
   val allSourceFiles = git.modifiedFiles + git.createdFiles

   // --- PR metadata checks ---
   val title = github.pullRequest.title
   val body = github.pullRequest.body ?: ""

   if (title.isBlank()) {
      warn("⚠️ PR title is empty — please describe what this change does.")
   }
   if (body.isBlank()) {
      warn("📝 Add a PR description for reviewers.")
   }

   if (git.modifiedFiles.size > 50) {
      fail("🚨 This PR changes ${git.modifiedFiles.size} files. Split it into smaller ones.")
   }

   // --- Test coverage reminder ---
   val hasTestChanges = allSourceFiles.any { it.contains("Test") || it.contains("Spec") }
   val hasSourceChanges = allSourceFiles.any { it.endsWith(".kt") && !it.contains("/test/") }
   if (hasSourceChanges && !hasTestChanges) {
      warn("🧪 You modified code but added no tests.")
   }

   // --- Dependency changes ---
   val dependencyFiles = listOf("build.gradle.kts", "libs.versions.toml", "settings.gradle.kts")
   val changedDependencies = allSourceFiles.filter { f -> dependencyFiles.any { f.endsWith(it) } }
   if (changedDependencies.isNotEmpty()) {
      message("📦 Dependencies changed in: ${changedDependencies.joinToString()}")
   }

   // --- Static analysis integration (KtLint) ---
   fun checkReport(path: String, tool: String) {
      val file = File(path)
      if (file.exists()) {
         val issues = file.readText().split("<error").size - 1
         if (issues > 0) warn("⚠️ $tool found $issues issues. Check your report.")
         else message("✅ $tool is clean.")
      }
   }

   checkReport("build/reports/ktlint/ktlint-aggregated.xml", "KtLint")
   checkReport("build/reports/detekt/detekt-aggregated.xml", "Detekt")
}
