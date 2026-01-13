import java.io.FileInputStream
import java.util.Properties

plugins {
    id("catchmate.android.library")
    id("catchmate.android.hilt")
    id("catchmate.kotlin.hilt")
}

val properties = Properties()
properties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.catchmate.data"

    val serverDomain = properties["server_domain"] as? String ?: ""
    val googleWebClientId = properties["google_web_client_id"] as? String ?: ""

    defaultConfig {
        buildConfigField("String", "SERVER_DOMAIN", serverDomain)
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", googleWebClientId)
    }

    buildFeatures {
        buildConfig = true
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.google.api.client)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.androidx.security.crypto.ktx)
}
