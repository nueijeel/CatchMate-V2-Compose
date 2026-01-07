plugins {
    id("catchmate.android.library")
    id("kotlin-parcelize")
}

android {
    namespace = "com.catchmate.domain"
}

dependencies {
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.stomp)
}
