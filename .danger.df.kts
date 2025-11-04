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

danger(args) {
   val allSourceFiles = git.modifiedFiles + git.createdFiles
   // --- PR metadata checks ---
   if (pullRequest.title.isBlank()) {
      warn("⚠️ PR title is empty — please describe what this change does.")
   }
   if (pullRequest.body.isBlank()) {
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
   val ktLintReport = File("build/reports/ktlint/ktlint-aggregated.xml")
   if (ktLintReport.exists()) {
      val warnings = ktLintReport.readText().split("<error").size - 1
      if (warnings > 0) {
         warn("⚠️ KtLint found $warnings issues. Check your report.")
      } else {
         message("✅ KtLint is clean.")
      }
   }
   // --- Static analysis integration (Detekt) ---
   val detektReport = File("build/reports/detekt/detekt-aggregated.xml")
   if (detektReport.exists()) {
      val warnings = detektReport.readText().split("<error").size - 1
      if (warnings > 0) {
         warn("⚠️ Detekt found $warnings issues. Check your report.")
      } else {
         message("✅ Detekt is clean.")
      }
   }
}
