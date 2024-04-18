package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("""
        SELECT * FROM airport
        WHERE name NOT LIKE :departingAirport
        ORDER BY passengers DESC
    """)
    fun getDestinationAirports(departingAirport: String): Flow<List<Airport>>

    @Query("""
        SELECT * FROM airport
        WHERE name LIKE '%' || :search || '%' 
        OR iata_code LIKE '%' || :search || '%'
        ORDER BY passengers DESC
    """)
    fun searchAirports(search: String): Flow<List<Airport>>

    @Query("""
        SELECT * FROM airport
        WHERE iata_code LIKE :iataCode
        LIMIT 1
    """)
    fun getAirport(iataCode: String): Flow<Airport>

    @Query("""
        SELECT * FROM favorite
        WHERE departure_code like :departureCode AND 
        destination_code like :destinationCode
    """)
    fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite>

    @Insert
    suspend fun addFavorite(favorite: Favorite)

    @Delete
    suspend fun removeFavorite(favorite: Favorite)

    @Query("""
        SELECT * FROM favorite
    """)
    fun getFavoriteFlights(): Flow<List<Favorite>>
}