@file:Suppress("UNUSED_VARIABLE")

//import com.github.gradle.node.task.NodeTask

//import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation

plugins {
    kotlin("multiplatform")
    id("com.gradleup.shadow").version("8.3.6")
//    id("com.github.node-gradle.node")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("MainKt")
}

kotlin {
    jvm {
//        jvmToolchain(8)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_1_8)
//        }
    }
    js {
        nodejs {
            dependencies {
                npm {

                }
            }
        }
        binaries.executable()
    }

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native") { }
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.binaries { executable { baseName = "taglinter" } }

    sourceSets {
        val okioVersion = "3.10.2"
        val kotlinLoggingVersion = "7.0.3"
        val commonMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio:$okioVersion")
                implementation("com.github.ajalt.clikt:clikt:4.2.1")
                implementation("com.github.ajalt.mordant:mordant:2.1.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("io.github.oshai:kotlin-logging:$kotlinLoggingVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
//                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
//                implementation("com.github.ajalt.mordant:mordant:2.1.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
                implementation("ch.qos.logback:logback-core:1.5.6")
                implementation("ch.qos.logback:logback-classic:1.5.6")
                implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
                implementation("com.squareup.okio:okio-nodefilesystem:$okioVersion")
                implementation("io.github.oshai:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
        val jsTest by getting
        val nativeMain by getting {
            dependencies {
                implementation("io.github.oshai:kotlin-logging-linuxx64:$kotlinLoggingVersion")
            }
        }
//        val nativeTest by getting
    }
}

//val packAction by tasks.creating(NodeTask::class) {
//    group = "distribution"
//    dependsOn(compileKotlinJs, rootPackageJson, tasks.npmInstall)
//    script.set(file("node_modules/webpack-cli/bin/cli.js"))
//    args.set(listOf("-c", generateWebpackConfig.outputConfig.absolutePath))
//}