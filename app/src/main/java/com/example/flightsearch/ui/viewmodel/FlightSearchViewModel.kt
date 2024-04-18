package com.example.flightsearch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.AirportRepository
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.Flight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightSearchViewModel(private val airportRepository: AirportRepository) : ViewModel() {
    private val _uiState by lazy { MutableStateFlow(FlightUIState()) }
    val uiState: StateFlow<FlightUIState> = _uiState

    init {
        // update favorite flights list on launch to display when search is empty
        getFavoriteFlights()
    }

    fun search(searchString: String) {
        viewModelScope.launch {
            // Query database for airports with "searchString" in their name or iata code
            airportRepository.searchAirports(searchString).collect { airports ->
                _uiState.update {
                    // Update UI state with a list of matching airports
                    it.copy(searchString = searchString,  departingAirport = null, airportsList = airports)
                }
            }
        }
    }

    fun updateDepartingAirport(departingAirport: Airport) {
        viewModelScope.launch {
            airportRepository.getDestinationAirports(departingAirport.name).collect { airports ->
                // Create a flight from the origin airport to every other possible airport
                val flightsList: MutableList<Flight> = mutableListOf()
                for (airport in airports) {
                    flightsList.add(Flight(departingAirport, airport))
                }
                // Update UI state with a list of all valid flights
                _uiState.update {
                    it.copy(departingAirport = departingAirport, flightsList = flightsList)
                }
            }
        }
    }

    fun toggleFavorite(flight: Flight) {
        viewModelScope.launch {
            val departureCode = flight.origin.iata
            val destinationCode = flight.destination.iata

            // try to get favorite from the database
            val favorite: Favorite? = airportRepository.getFavorite(
                departureCode = departureCode,
                destinationCode = destinationCode
            ).first()

            // if it doesn't exist, add it, otherwise remove
            if (favorite == null)
                airportRepository.addFavorite(
                    Favorite(
                        departureCode = departureCode,
                        destinationCode = destinationCode
                    )
                )
            else
                airportRepository.removeFavorite(favorite)
        }
    }

    private fun getFavoriteFlights() {
        viewModelScope.launch {
            airportRepository.getFavoriteFlights().collect { favorites ->
                val favoritesList: MutableList<Flight> = mutableListOf()
                for (favorite in favorites) {
                    // get flight information from the iata codes
                    var origin: Airport? = null
                    var destination: Airport? = null
                    airportRepository.getAirport(favorite.departureCode).collect { origin = it }
                    airportRepository.getAirport(favorite.destinationCode).collect { destination = it }
                    // add to favorites as long as both airports were found
                    if (origin != null && destination != null)
                        favoritesList.add(Flight(origin = origin!!, destination = destination!!, favorite = true))
                }

                _uiState.update {
                    it.copy(favoritesList = favoritesList)
                }
            }
        }
    }

    // viewmodel initializer
    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(application.airportRepository)
            }
        }
    }
}

data class FlightUIState(
    var searchString: String = "",
    var airportsList: List<Airport> = emptyList(),
    var departingAirport: Airport? = null,
    var flightsList: List<Flight> = emptyList(),
    var favoritesList: List<Flight> = emptyList()
)