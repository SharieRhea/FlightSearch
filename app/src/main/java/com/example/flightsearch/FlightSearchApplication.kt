package com.example.flightsearch

import android.app.Application
import com.example.flightsearch.data.AirportLocalDataSource
import com.example.flightsearch.data.AirportRepository

class FlightSearchApplication : Application() {
    private val database: AirportLocalDataSource by lazy { AirportLocalDataSource.getDatabase(this) }
    val airportRepository: AirportRepository by lazy { AirportRepository(database) }
}