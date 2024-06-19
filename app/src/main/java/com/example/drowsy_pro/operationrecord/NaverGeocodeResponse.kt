package com.example.drowsy_pro.operationrecord

data class NaverGeocodeResponse(
    val results: List<GeocodeResult>
)

data class GeocodeResult(
    val region: Region,
    val land: Land
)

data class Region(
    val area1: Area,
    val area2: Area,
    val area3: Area
)

data class Area(
    val name: String
)

data class Land(
    val number1: String,
    val number2: String,
    val addition0: Addition
)

data class Addition(
    val value: String
)
