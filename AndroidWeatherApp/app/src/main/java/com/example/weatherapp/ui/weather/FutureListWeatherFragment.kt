package com.example.weatherapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherApiService
import com.example.weatherapp.data.internal.UnitSystem
import com.example.weatherapp.data.provider.LocationProvider
import com.example.weatherapp.data.provider.UnitProvider
import com.example.weatherapp.data.response.ForecastResponse
import com.example.weatherapp.data.response.X
import com.google.android.gms.location.FusedLocationProviderClient
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.future_list_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException


class FutureListWeatherFragment : Fragment() {

    companion object {
        fun newInstance() =
            FutureListWeatherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Forecast"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Weather for a week"
        return inflater.inflate(R.layout.future_list_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val apiService = WeatherApiService()
        val unitProvider = context?.let { UnitProvider(it) }
        val units = unitProvider?.getUnitSystem()
        val queryParam = if (units?.equals(UnitSystem.METRIC)!!) "metric" else "imperial"
        var responseFromApi: ForecastResponse

        GlobalScope.launch(Dispatchers.Main) {
            val locationProvider =
                context?.let { LocationProvider(FusedLocationProviderClient(it), it) }
            val locationName = locationProvider?.getPreferredLocationString()

            try {
                responseFromApi = if (locationName!!.contains(",")) {
                    val lat = locationName.split(",")[0].split(".")[0]
                    val lon = locationName.split(",")[1].split(".")[0]
                    apiService.getForecastByCoords(lat, lon, 40, queryParam).await()
                } else
                    apiService.getForecastWeather(locationName, 40, queryParam).await()
                handleResponse(responseFromApi)
            } catch (exc: HttpException) {
                Toast.makeText(context, "Wrong location!", Toast.LENGTH_LONG).show()
                responseFromApi = apiService.getForecastWeather("Moscow", 40, queryParam).await()
                handleResponse(responseFromApi)
            }

        }
    }

    private fun handleResponse(response: ForecastResponse?) {
        val forecastDays = response?.list
        val currentLocationResponse = response?.city?.name

        if (currentLocationResponse != null) {
            (activity as? AppCompatActivity)?.supportActionBar?.title =
                currentLocationResponse
        }
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Week"

        val unitProvider = context?.let { UnitProvider(it) }
        val units = unitProvider?.getUnitSystem()

        initRecyclerView(forecastDays!!.toFutureWeatherItems(units!!))
    }

    private fun List<X>.toFutureWeatherItems(units: UnitSystem): List<FutureWeatherItem> {
        return this.map {
            FutureWeatherItem(it, units)
        }
    }

    private fun initRecyclerView(items: List<FutureWeatherItem>) {
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FutureListWeatherFragment.context)
            adapter = groupAdapter
        }

        groupAdapter.setOnItemClickListener { item, view ->
            Toast.makeText(this@FutureListWeatherFragment.context, "Clicked", Toast.LENGTH_SHORT)
                .show()
        }
    }

}
