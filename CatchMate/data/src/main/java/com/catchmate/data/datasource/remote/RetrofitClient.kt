package com.catchmate.data.datasource.remote

import com.catchmate.data.BuildConfig
import com.catchmate.data.datasource.local.LocalStorageDataSource
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Inject

class RetrofitClient
    @Inject
    constructor(
        localStorageDataSource: LocalStorageDataSource,
        authAuthenticator: AuthAuthenticator,
    ) {
        private val logging =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        private val authInterceptor =
            AuthInterceptor(localStorageDataSource)

        private val nullOnEmptyConverterFactory =
            object : Converter.Factory() {
                fun converterFactory() = this

                override fun responseBodyConverter(
                    type: Type,
                    annotations: Array<out Annotation>,
                    retrofit: Retrofit,
                ) = object : Converter<ResponseBody, Any?> {
                    val nextResponseBodyConverter =
                        retrofit.nextResponseBodyConverter<Any?>(
                            converterFactory(),
                            type,
                            annotations,
                        )

                    override fun convert(value: ResponseBody) =
                        if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
                }
            }

        private val okHttpClient =
            OkHttpClient
                .Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .authenticator(authAuthenticator)
                .build()

        val retrofit: Retrofit by lazy {
            Retrofit
                .Builder()
                .baseUrl(BuildConfig.SERVER_DOMAIN)
                .client(okHttpClient)
                .addConverterFactory(nullOnEmptyConverterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        inline fun <reified T> createApi(): T = retrofit.create(T::class.java)
    }
