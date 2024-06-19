package com.example.drowsy_pro.sleeplog

data class sleeplogdata(
    val sleep_time:String,
    val sleep_location_lati:Float,
    val sleep_location_longi:Float,
    val id:Int
)
data class requestsleep(
    val jwt:String
)

