plugins {
    alias(libs.plugins.convention.application.android)
}

dependencies {
    implementation(project(":core:data:platform"))
    implementation(project(":core:data:repository"))
    implementation(libs.compose.activity)
    implementation(libs.koin.core)
}