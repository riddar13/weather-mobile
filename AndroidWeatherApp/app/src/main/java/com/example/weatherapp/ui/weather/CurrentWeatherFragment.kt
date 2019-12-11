package com.example.weatherapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherApiService
import com.example.weatherapp.data.internal.UnitSystem
import com.example.weatherapp.data.provider.LocationProvider
import com.example.weatherapp.data.provider.UnitProvider
import com.example.weatherapp.data.response.CurrentWeatherResponse
import com.example.weatherapp.ui.GlideApp
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CurrentWeatherFragment : Fragment() {

    companion object {
        fun newInstance() =
            CurrentWeatherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Current Weather"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Today"
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val apiService = WeatherApiService()

        GlobalScope.launch(Dispatchers.Main) {
            val locationProvider =
                context?.let { LocationProvider(FusedLocationProviderClient(it), it) }
            val locationName = locationProvider?.getPreferredLocationString()
            val unitProvider = context?.let { UnitProvider(it) }
            val units = unitProvider?.getUnitSystem()
            val queryParam = if (units?.equals(UnitSystem.METRIC)!!) "metric" else "imperial"
            val responseFromApi: CurrentWeatherResponse

            try {
                responseFromApi = if (locationName!!.contains(",")) {
                    val lat = locationName.split(",")[0].split(".")[0]
                    val lon = locationName.split(",")[1].split(".")[0]
                    apiService.getCurrentByCoords(lat, lon, queryParam).await()
                } else
                    apiService.getCurrentWeather(locationName, queryParam).await()

                handleResponse(responseFromApi)
            } catch (exc: HttpException) {
                exc.printStackTrace()
                Toast.makeText(context, "Wrong location!", Toast.LENGTH_LONG).show()
                val response = apiService.getCurrentWeather("Moscow", queryParam).await()
                handleResponse(response)
            }

        }
    }

    private fun handleResponse(response: CurrentWeatherResponse?) {
        val currentLocationResponse = response?.name

        if (currentLocationResponse != null) {
            (activity as? AppCompatActivity)?.supportActionBar?.title =
                currentLocationResponse
        }
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Today"

        if (response != null) {
            GlideApp.with(this@CurrentWeatherFragment)
                .load("https://openweathermap.org/img/w/${response.weather[0].icon}.png")
                .into(imageView_condition_icon)
        }

        val unitProvider = context?.let { UnitProvider(it) }
        val units = unitProvider?.getUnitSystem()

        if (units?.equals(UnitSystem.METRIC)!!)
            response?.let { updateAsMetric(it) }
        else
            response?.let { updateAsImperial(it) }
    }


    private fun updateAsImperial(current: CurrentWeatherResponse) {
        textView_temperature.text = "${current.main.temp}°F"
        textView_condition.text = current.weather[0].main
        textView_feels_like_temperature.text =
            "Feels like ${current.main.temp_min}°F"
        textView_wind.text =
            "Wind:  ${current.wind.deg}°, ${current.wind.speed} mp/h"
        textView_precipitation.text =
            "Pressure: ${current.main.pressure} mbar"
    }

    private fun updateAsMetric(current: CurrentWeatherResponse) {
        textView_temperature.text = "${current.main.temp}°C"
        textView_condition.text = current.weather[0].main
        textView_feels_like_temperature.text =
            "Feels like ${current.main.temp_min}°C"
        textView_wind.text =
            "Wind:  ${current.wind.deg}°, ${current.wind.speed} km/h"
        textView_precipitation.text =
            "Pressure: ${current.main.pressure} mbar"
    }

}
