package com.catchmate.app

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

internal fun Project.configureKotlinAndroid() {
    // Plugins
    pluginManager.apply("org.jetbrains.kotlin.android")

    // Android Settings
    (androidExtension as BaseExtension).apply {
        compileSdkVersion(35)

        defaultConfig {
            minSdk = 31
            targetSdk = 35
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            isCoreLibraryDesugaringEnabled = true
        }

        buildTypes {
            getByName("debug") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
            getByName("release") {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules-release.pro",
                )
            }
        }
    }

    configureKotlin()

    val libs = extensions.libs

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    }
}

internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            allWarningsAsErrors.set(findProperty("warningAsErrors") == "true")
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        }
    }
    configure<KotlinProjectExtension> {
        jvmToolchain(17)
    }
}
