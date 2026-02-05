plugins {
    alias(libs.plugins.convention.application.android)
}

dependencies {
    implementation(project(":modules.shared"))
    implementation(libs.tuucho.android)
    implementation(libs.compose.ui.android)
    implementation(libs.compose.foundation.android)
    implementation(libs.compose.activity)
    implementation(libs.koin.core)
}
