plugins {
    alias(libs.plugins.convention.application.android)
}

dependencies {
    implementation(project(":sample:shared"))
    implementation(project(":core:data:repository"))
    implementation(libs.compose.activity)
    implementation(libs.koin.core)
}