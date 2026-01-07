import java.io.FileInputStream
import java.util.Properties

plugins {
    id("catchmate.android.library")
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

val properties = Properties()
properties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.catchmate.presentation"

    val serverSocketUrl = properties["server_socket_url"] as? String ?: ""

    defaultConfig {
        buildConfigField("String", "SERVER_SOCKET_URL", serverSocketUrl)
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)
    implementation(libs.legacy.support.v4)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.circleimageview)
    implementation(libs.glide)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.stomp)
    implementation(libs.androidx.core.splashscreen)
}
