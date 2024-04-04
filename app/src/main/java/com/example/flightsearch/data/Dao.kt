package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("""
        SELECT * FROM airport
        WHERE name LIKE '%' || :search || '%' 
        OR iata_code LIKE '%' || :search || '%'
        ORDER BY passengers DESC
    """)
    fun searchAirports(search: String): Flow<List<Airport>>
}