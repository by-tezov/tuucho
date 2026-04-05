plugins {
    alias(libs.plugins.convention.application.jvm)
}

compose.desktop {
    application {
        mainClass = "com.tezov.tuucho.sample.MainScreenKt"
    }
}

dependencies {
    implementation(project(":modules.shared"))
    implementation(libs.tuucho.jvm)
    implementation(compose.desktop.currentOs)
    implementation(libs.koin.core)
    implementation(libs.kotlin.couroutine)
    implementation(libs.kotlin.couroutine.swing)
}
