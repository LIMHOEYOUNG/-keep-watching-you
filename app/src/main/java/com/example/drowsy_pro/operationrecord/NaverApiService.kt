package com.example.drowsy_pro.operationrecord

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverApiService {
    @GET("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc")
    fun getReverseGeocoding(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String,
        @Query("request") requestType: String = "coordsToaddr",
        @Query("coords") coords: String,
        @Query("sourcecrs") sourceCrs: String = "epsg:4326",
        @Query("output") output: String = "json",
        @Query("orders") orders: String = "legalcode,admcode"
    ): Call<NaverGeocodeResponse>
}