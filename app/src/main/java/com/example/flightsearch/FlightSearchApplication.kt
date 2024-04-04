package com.example.flightsearch

import android.app.Application
import com.example.flightsearch.data.AirportDatabase

class FlightSearchApplication : Application() {
    val database: AirportDatabase by lazy { AirportDatabase.getDatabase(this) }
}