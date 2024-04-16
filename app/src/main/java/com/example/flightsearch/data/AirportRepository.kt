package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class AirportRepository(private val airportLocalDataSource: AirportLocalDataSource) {

    val airports: Flow<List<Airport>> = airportLocalDataSource.dao().getAllAirports()

    fun searchAirports(search: String): Flow<List<Airport>> = airportLocalDataSource.dao().searchAirports(search)

}