plugins {
    alias(libs.plugins.convention.application.android)
}

dependencies {
    implementation(project(":shared"))
//    implementation(project(":core:data:repository")) //TODO
    implementation(libs.compose.activity)
    implementation(libs.koin.core)
}