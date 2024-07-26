package com.supremebeing.phoneanti.aichat.network.client

import com.supremebeing.phoneanti.aichat.network.server.HttpService
import com.supremebeing.phoneanti.aichat.tool.serverIP
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 *@ClassName: RetrofitClient
 *@Description: Retrofit客户端
 *@Author: Yqw
 *@Date: 2022/12/4 17:13
**/
object RetrofitClient {
    private var retrofit : Retrofit? = null

    val serviceApi : HttpService by lazy { getRetrofit() !!.create(HttpService::class.java) }

    private fun getRetrofit() : Retrofit?{
        if (retrofit == null){
            synchronized(RetrofitClient::class.java){
                retrofit = Retrofit.Builder()
                    .baseUrl(serverIP)
                    .client(CustomOkHttpClient.getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build()
            }
        }
        return retrofit
    }

}