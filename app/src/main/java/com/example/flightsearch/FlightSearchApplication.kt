package com.example.flightsearch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightsearch.data.AirportLocalDataSource
import com.example.flightsearch.data.AirportRepository
import com.example.flightsearch.data.SearchBarRepository

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "searchbarContent")

class FlightSearchApplication : Application() {
    private val database: AirportLocalDataSource by lazy { AirportLocalDataSource.getDatabase(this) }
    val airportRepository: AirportRepository by lazy { AirportRepository(database) }
    lateinit var searchBarRepository: SearchBarRepository

    override fun onCreate() {
        super.onCreate()
        searchBarRepository = SearchBarRepository(dataStore)
    }
}