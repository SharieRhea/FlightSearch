package com.example.flightsearch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airport")
data class Airport(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "iata_code")
    val iata: String,
    val passengers: Int
)

data class Flight(val origin: Airport, val destination: Airport)