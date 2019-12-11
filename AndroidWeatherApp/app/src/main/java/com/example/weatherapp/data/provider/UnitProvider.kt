package com.example.weatherapp.data.provider

import android.content.Context
import com.example.weatherapp.data.internal.UnitSystem

const val UNIT_SYSTEM = "UNIT_SYSTEM"

class UnitProvider(context: Context) : PreferenceProvider(context) {

    fun getUnitSystem(): UnitSystem {
        val selectedName = preferences.getString(UNIT_SYSTEM, UnitSystem.METRIC.name)
        return UnitSystem.valueOf(selectedName!!)
    }
}