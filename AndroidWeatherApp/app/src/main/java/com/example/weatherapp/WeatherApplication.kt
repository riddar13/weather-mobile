package com.example.weatherapp

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.example.weatherapp.data.provider.LocationProvider
import com.example.weatherapp.data.provider.UnitProvider
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class WeatherApplication : Application(), KodeinAware {

    override val kodein: Kodein
        get() = Kodein.lazy {
            import(androidXModule(this@WeatherApplication))

            bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
            bind<LocationProvider>() with singleton { LocationProvider(instance(), instance()) }
            bind<UnitProvider>() with singleton { UnitProvider(instance()) }
        }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }

}