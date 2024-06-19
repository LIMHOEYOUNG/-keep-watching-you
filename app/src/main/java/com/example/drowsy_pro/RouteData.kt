package com.example.drowsy_pro

import com.google.gson.annotations.SerializedName

data class LocationData(
    val time: String,
    val latitude: Double,
    val longitude: Double
)

data class RouteData(
    @SerializedName("date")
    val locations: List<LocationData>
)