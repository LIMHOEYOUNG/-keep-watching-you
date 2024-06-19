package com.example.drowsy_pro.operationrecord

data class drivingdata(
    var start_location_lati:Float,//위도
    var start_location_longi:Float,//경도
    var end_location_lati:Float,
    var end_location_longi:Float,
    var start_time:String,
    var end_time:String,
    var id:Int
)
data class requestdriving(
    var jwt:String
)
data class requestdrivingmap(
    var jwt:String,
    var id: Int
)
data class RouteData(
    val date: List<mapLocation>
)

data class mapLocation(
    val time: String,
    val latitude: Double,
    val longitude: Double
)