// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    base
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.multiplatform.library) apply false
    alias(libs.plugins.koin) apply false

    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
}