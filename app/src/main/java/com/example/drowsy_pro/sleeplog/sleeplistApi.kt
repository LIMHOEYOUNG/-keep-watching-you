package com.example.drowsy_pro.sleeplog

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface sleeplistApi {
    @POST("user_sleep/sleep_get_list")
    fun getSleepRecords(@Body request: requestsleep): Call<List<sleeplogdata>>

    companion object {
        private const val BASE_URL = "http://3.39.187.161:8000/"

        fun create(): sleeplistApi {

            val gson : Gson =  GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(sleeplistApi::class.java)
        }
    }
}