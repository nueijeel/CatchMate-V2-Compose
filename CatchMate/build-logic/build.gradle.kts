plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "catchmate.android.hilt"
            implementationClass = "com.catchmate.app.HiltAndroidPlugin"
        }
        register("kotlinHilt") {
            id = "catchmate.kotlin.hilt"
            implementationClass = "com.catchmate.app.HiltKotlinPlugin"
        }
    }
}
