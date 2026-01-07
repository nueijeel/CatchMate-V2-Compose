package com.catchmate.app

import com.android.build.gradle.TestedExtension
import org.gradle.api.Project

internal fun Project.configureKotestAndroid() {
    configureKotest()
    configureJUnitAndroid()
}

internal fun Project.configureJUnitAndroid() {
    val extension = androidExtension as TestedExtension

    extension.testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}
