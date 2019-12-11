package com.example.weatherapp.data.response

data class ForecastResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<X>,
    val message: Int
)