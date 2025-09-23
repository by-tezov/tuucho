plugins {
    alias(libs.plugins.convention.application.android)
}

dependencies {
    implementation(project(":app:kmm"))
    implementation(project(":core:data:repository"))
    implementation(libs.compose.activity)
    implementation(libs.koin.core)
}