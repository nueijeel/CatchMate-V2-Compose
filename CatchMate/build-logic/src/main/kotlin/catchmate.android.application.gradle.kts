import com.catchmate.app.configureHiltAndroid
import com.catchmate.app.configureKotestAndroid
import com.catchmate.app.configureKotlinAndroid

plugins {
    id("com.android.application")
}

configureKotlinAndroid()
configureHiltAndroid()
configureKotestAndroid()
