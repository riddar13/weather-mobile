package com.example.weatherapp.data.response

data class X(
    val clouds: CloudsX,
    val dt: Int,
    val dt_txt: String,
    val main: MainX,
    val sys: SysX,
    val weather: List<WeatherX>,
    val wind: WindX
)