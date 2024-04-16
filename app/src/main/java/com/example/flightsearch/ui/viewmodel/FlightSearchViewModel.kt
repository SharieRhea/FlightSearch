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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightSearchViewModel(private val airportRepository: AirportRepository) : ViewModel() {
    // uiState for the search bar and search results screen
    private val _uiState by lazy { MutableStateFlow(FlightUIState()) }
    val uiState: StateFlow<FlightUIState> = _uiState

    fun search(search: String) {
        viewModelScope.launch {
            airportRepository.searchAirports(search).collect { results ->
                _uiState.update {
                    it.copy(search = search, airportSearchResultsList = results)
                }
            }
        }
    }

    fun updateDepartingAirport(airport: Airport) {
        _uiState.update {
            it.copy(departingAirport = airport)
        }
    }

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
    var search: String = "",
    var airportSearchResultsList: List<Airport> = emptyList(),
    var departingAirport: Airport? = null
)