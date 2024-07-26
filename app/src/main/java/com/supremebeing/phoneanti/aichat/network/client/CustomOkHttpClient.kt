package com.supremebeing.phoneanti.aichat.network.client

import com.supremebeing.phoneanti.aichat.BuildConfig
import com.supremebeing.phoneanti.aichat.network.intercepter.HttpLogger
import com.supremebeing.phoneanti.aichat.tool.AIP
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 *   Name: CustomOkHttpClient
 *   CreateBy: dww On 2021/8/5
 *   Copyright:
 *   Language: Kotlin
 *   Description: OkHttp客户端
 */
class CustomOkHttpClient {

    companion object {
        fun getClient(): OkHttpClient {
            if (client == null) {
                create()
            }
            return client !!
        }

        private var client: OkHttpClient? = null

        private fun create() {
            val httpLogger = HttpLoggingInterceptor(HttpLogger()).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            //设置Header
            val interceptor = Interceptor {
                val builder = it.request().newBuilder().apply {
                    addHeader("Content-Type", "application/json")
                    addHeader("Authorization", "Bearer "+ AIP.token)
                }
                it.proceed(builder.build())
            }

            client = OkHttpClient.Builder().apply {
                addInterceptor(interceptor)

                if (BuildConfig.DEBUG) { //debug模式添加Log日志
                    addNetworkInterceptor(httpLogger)
                }

                retryOnConnectionFailure(true)
                readTimeout(15, TimeUnit.SECONDS)
                connectTimeout(15, TimeUnit.SECONDS)
            }.build()
        }
    }
}