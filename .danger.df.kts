@file:DependsOn("io.github.bastosss77:ktlint-danger-kotlin:0.5.1") // kotlinc 2.2.20 / Java 21

import io.github.bastosss77.danger.ktlint.KtlintPlugin
import systems.danger.kotlin.*
import java.io.File

register.plugin(KtlintPlugin)
danger(args) {
    reportWithKtLintPlugin()
}

fun reportWithKtLintPlugin() {
    with(KtlintPlugin) {
        val report = File("build/reports/ktlint/ktlint-aggregated.xml")
        report(parse(report))
    }
}