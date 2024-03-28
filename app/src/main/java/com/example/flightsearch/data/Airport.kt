package com.example.flightsearch.data

data class Flight(val origin: Airport, val destination: Airport)

data class Airport(val IATA: String, val name: String)
