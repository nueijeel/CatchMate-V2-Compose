package com.catchmate.android

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        // kakao sdk 초기화
        KakaoSdk.init(this, "${BuildConfig.KAKAO_NATIVE_APP_KEY}")

        // naver sdk 초기화
        NaverIdLoginSDK.initialize(
            this,
            "${BuildConfig.NAVER_CLIENT_ID}",
            "${BuildConfig.NAVER_CLIENT_SECRET}",
            "CatchMate",
        )

        // FirebaseAnalytics instance 초기화
        firebaseAnalytics = Firebase.analytics
    }
}
