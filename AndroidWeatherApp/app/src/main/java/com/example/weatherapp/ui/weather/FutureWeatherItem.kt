package com.example.weatherapp.ui.weather

import com.example.weatherapp.R
import com.example.weatherapp.data.internal.UnitSystem
import com.example.weatherapp.data.response.X
import com.example.weatherapp.ui.GlideApp
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_future_weather.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureWeatherItem(
    val forecastday: X,
    val unit: UnitSystem
) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            textView_condition.text = forecastday.weather[0].main
            updateDate()
            updateTemperature()
            updateConditionImage()
        }
    }

    override fun getLayout() = R.layout.item_future_weather

    private fun ViewHolder.updateDate() {
        val dtFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        textView_date.text = forecastday.dt_txt.format(dtFormatter) // TODO
    }

    private fun ViewHolder.updateTemperature() {
        if (unit == UnitSystem.METRIC)
            textView_temperature.text = "${forecastday.main.temp}°C"
        else
            textView_temperature.text = "${forecastday.main.temp}°F"
    }

    private fun ViewHolder.updateConditionImage() {
        GlideApp.with(this.containerView)
            .load("https://openweathermap.org/img/w/${forecastday.weather[0].icon}.png")
            .into(imageView_condition_icon)
    }
}