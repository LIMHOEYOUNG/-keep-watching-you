package com.example.drowsy_pro.unpersonlog

import com.example.drowsy_pro.unpersondata.putunpersonlist
import com.example.drowsy_pro.unpersondata.unpersondata
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface unpersonlistApi {
    @POST("user_unknown/unknown_get_list")
    fun getunpersonlist(@Body request: putunpersonlist): Call<List<unpersondata>>

    companion object {
        private const val BASE_URL = "http://3.39.187.161:8000/"

        fun create(): unpersonlistApi {
            val gson : Gson =  GsonBuilder().setLenient().create();
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(unpersonlistApi::class.java)
        }
    }
}