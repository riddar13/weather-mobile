package com.example.weatherapp.data.response

data class MainX(
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_kf: Int,
    val temp_max: Double,
    val temp_min: Double
)