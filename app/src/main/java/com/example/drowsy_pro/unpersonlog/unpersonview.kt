package com.example.drowsy_pro.unpersonlog

import com.example.drowsy_pro.unpersondata.putunperson
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface unpersonview {
        @Headers("Content-Type: application/json")
        @POST("/user_unknown/unknown_get")
        fun getunperson(@Body requestData: putunperson): Call<ResponseBody>
    }
 object ApiServiceBuilder {
     private val retrofit: Retrofit = Retrofit.Builder()
         .baseUrl("http://3.39.187.161:8000")
         .addConverterFactory(GsonConverterFactory.create())
         .client(OkHttpClient.Builder().build())
         .build()
        val apiService: unpersonview = retrofit.create(unpersonview::class.java)
 }

