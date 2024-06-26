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
import com.example.flightsearch.data.SearchBarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FlightSearchViewModel(private val airportRepository: AirportRepository, private val searchBarRepository: SearchBarRepository) : ViewModel() {
    private val _uiState by lazy { MutableStateFlow(FlightUIState()) }
    val uiState: StateFlow<FlightUIState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.update {
                // get the user's last search contents from the DataStore
                val searchContent: String = getSearchContent()
                // populate the airports list for display search results
                search(searchContent)
                // update UI state so that the search bar will display the last search contents on init
                it.copy(searchString = searchContent)
            }
            // update favorite flights list on launch to display when search is empty
            getFavoriteFlights()
        }
    }

    fun getSearchContent(): String {
        val searchContent: String
        runBlocking {
            searchContent = searchBarRepository.getSearchContent().first()
        }
        return searchContent
    }

    fun search(searchString: String) {
        viewModelScope.launch {
            searchBarRepository.saveSearchContent(searchString)
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
                    if (getFavorite(departingAirport.iata, airport.iata) == null)
                        flightsList.add(Flight(departingAirport, airport))
                    else
                        flightsList.add(Flight(departingAirport, airport, favorite = true))
                }
                // Update UI state with a list of all valid flights
                _uiState.update {
                    it.copy(departingAirport = departingAirport, flightsList = flightsList)
                }
            }
        }
    }

    private suspend fun getFavorite(departureCode: String, destinationCode: String): Favorite? {
        return airportRepository.getFavorite(
            departureCode = departureCode,
            destinationCode = destinationCode
        ).firstOrNull()
    }

    fun toggleFavorite(flight: Flight) {
        viewModelScope.launch {
            val favorite = getFavorite(flight.origin.iata, flight.destination.iata)
            // if it doesn't exist, add it, otherwise remove
            if (favorite == null) {
                flight.favorite = true
                airportRepository.addFavorite(
                    Favorite(
                        departureCode = flight.origin.iata,
                        destinationCode = flight.destination.iata
                    )
                )
                _uiState.value.favoritesList.add(flight)
            }
            else {
                airportRepository.removeFavorite(favorite)
                flight.favorite = false
                _uiState.value.favoritesList.remove(flight)
            }
        }
    }

    private suspend fun getFavoriteFlights() {
        airportRepository.getFavoriteFlights().collect { favorites ->
            val favoritesList: MutableList<Flight> = mutableListOf()
            for (favorite in favorites) {
                // get flight information from the iata codes
                val origin: Airport? = airportRepository.getAirport(favorite.departureCode).firstOrNull()
                val destination: Airport? = airportRepository.getAirport(favorite.destinationCode).firstOrNull()
                // add to favorites as long as both airports were found
                if (origin != null && destination != null)
                    favoritesList.add(Flight(origin = origin, destination = destination, favorite = true))
            }

            _uiState.update {
                it.copy(favoritesList = favoritesList)
            }
        }
    }

    // viewmodel initializer
    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(application.airportRepository, application.searchBarRepository)
            }
        }
    }
}

data class FlightUIState(
    var searchString: String = "",
    var airportsList: List<Airport> = emptyList(),
    var departingAirport: Airport? = null,
    var flightsList: List<Flight> = emptyList(),
    var favoritesList: MutableList<Flight> = mutableListOf()
)