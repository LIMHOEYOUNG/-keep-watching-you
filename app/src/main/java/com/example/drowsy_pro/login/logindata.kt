package com.example.drowsy_pro.login

data class logindata(
    var username : String?,
    var password :String?
)
data class resultlogin(
    var success :Int,
    var logintoken: String
)
data class tokenjwp(
    var jwt:String,
    var apptoken:String
)
data class tokensuccess(
    var success:Int
)
