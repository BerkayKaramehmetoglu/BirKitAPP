// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{
    dependencies {
        classpath(libs.androidx.androidx.navigation.safeargs.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
