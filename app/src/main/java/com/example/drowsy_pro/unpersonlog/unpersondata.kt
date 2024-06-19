package com.example.drowsy_pro.unpersondata

data class unpersondata(
    var id:Int,
    var userid_id:String,
    var time:String,
    var location_lati:Float,
    var location_longi:Float,
    var drive_log:String
)
data class putunperson(
    var jwt: String,
    var id:Int
)
data class putunpersonlist(
    var jwt:String
)