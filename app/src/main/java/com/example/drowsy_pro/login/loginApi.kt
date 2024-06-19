package com.example.drowsy_pro.login

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface loginApi {
    @POST("login/")
    fun login(@Body credentials: logindata): Call<resultlogin>

    companion object {
        private const val BASE_URL = "http://3.39.187.161:8000/"

        fun create(): loginApi {
            val gson : Gson =  GsonBuilder().setLenient().create();
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(loginApi::class.java)
        }
    }
}