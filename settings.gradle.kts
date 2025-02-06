pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        kotlin("multiplatform") version "2.1.10"
//        id("com.github.node-gradle.node") version "7.1.0"
    }
}

rootProject.name = "taglinter"
include("cli")