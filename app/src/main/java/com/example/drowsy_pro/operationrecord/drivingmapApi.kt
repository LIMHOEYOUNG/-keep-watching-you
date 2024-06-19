package com.example.drowsy_pro.operationrecord

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface drivingmapApi {
    @POST("user_log/log_get")
    fun getDrivingmap(@Body request: requestdrivingmap): Call<RouteData>

    companion object {
        private const val BASE_URL = "http://3.39.187.161:8000/"

        fun create(): drivingmapApi {

            val gson : Gson =  GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(drivingmapApi::class.java)
        }
    }
}

