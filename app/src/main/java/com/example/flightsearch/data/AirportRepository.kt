package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class AirportRepository(private val airportLocalDataSource: AirportLocalDataSource) {

    fun searchAirports(search: String): Flow<List<Airport>> = airportLocalDataSource.dao().searchAirports(search)

    fun  getDestinationAirports(departingAirport: String): Flow<List<Airport>> = airportLocalDataSource.dao().getDestinationAirports(departingAirport)

    fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite> = airportLocalDataSource.dao().getFavorite(departureCode, destinationCode)

    suspend fun addFavorite(favorite: Favorite) = airportLocalDataSource.dao().addFavorite(favorite)

    suspend fun removeFavorite(favorite: Favorite) = airportLocalDataSource.dao().removeFavorite(favorite)

    fun getFavoriteFlights(): Flow<List<Favorite>> = airportLocalDataSource.dao().getFavoriteFlights()

    fun getAirport(iataCode: String): Flow<Airport> = airportLocalDataSource.dao().getAirport(iataCode)

}