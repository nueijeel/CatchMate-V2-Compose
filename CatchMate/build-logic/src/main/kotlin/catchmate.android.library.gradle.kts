import com.catchmate.app.configureHiltAndroid
import com.catchmate.app.configureKotest
import com.catchmate.app.configureKotlinAndroid

plugins {
    id("com.android.library")
}

configureKotlinAndroid()
configureKotest()
configureHiltAndroid()
