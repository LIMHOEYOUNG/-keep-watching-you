package com.example.drowsy_pro.currentcar

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface carlocationApi {
    @POST("user_gps/gps_get")
    fun carlocation(@Body credentials: currentjwt): Call<currentdata>

    companion object {
        private const val BASE_URL = "http://3.39.187.161:8000/"

        fun create(): carlocationApi {
            val gson : Gson =  GsonBuilder().setLenient().create();
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(carlocationApi::class.java)
        }
    }
}