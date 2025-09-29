plugins {
    alias(libs.plugins.convention.application.android)
}

dependencies {
    implementation(project(":app:shared"))
    implementation(libs.tuucho.android)
    implementation(libs.compose.activity)
    implementation(libs.koin.core)
}