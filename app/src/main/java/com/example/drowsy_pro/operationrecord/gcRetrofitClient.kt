package com.example.drowsy_pro.operationrecord

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object gcRetrofitClient {
    private const val NAVER_BASE_URL = "https://naveropenapi.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(NAVER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun createNavergcService(): NaverApiService = retrofit.create(NaverApiService::class.java)
}