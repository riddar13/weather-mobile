package com.example.weatherapp.data

import com.example.weatherapp.data.response.CurrentWeatherResponse
import com.example.weatherapp.data.response.ForecastResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "7a48002d6ff9b15f4a192f0ec275c805"

interface WeatherApiService {

    @GET("weather")
    fun getCurrentWeather(
        @Query("q") location: String,
        @Query("units") units: String
    ): Deferred<CurrentWeatherResponse>

    @GET("weather")
    fun getCurrentByCoords(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String
    ): Deferred<CurrentWeatherResponse>

    @GET("forecast")
    fun getForecastWeather(
        @Query("q") location: String,
        @Query("cnt") daysAmount: Int,
        @Query("units") units: String
    ): Deferred<ForecastResponse>

    @GET("forecast")
    fun getForecastByCoords(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("cnt") daysAmount: Int,
        @Query("units") units: String
    ): Deferred<ForecastResponse>

    companion object {
        operator fun invoke(): WeatherApiService {
            val requestInterceptor = Interceptor { chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("appid", API_KEY)
                    .build()

                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }

}