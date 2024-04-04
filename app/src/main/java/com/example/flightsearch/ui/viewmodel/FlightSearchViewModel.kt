package com.example.flightsearch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Dao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

class FlightSearchViewModel(private val dao: Dao) : ViewModel() {
    // uiState for the search bar and search results screen
    private val _uiState = MutableStateFlow(SearchUIState())
    val uiState: StateFlow<SearchUIState> = _uiState.asStateFlow()

    fun getAirportList(): Flow<List<Airport>> {
        return _uiState.value.airportList
    }

    fun search(search: String) {
        _uiState.value.search = search
        _uiState.value.airportList = searchAirports(search)
    }
    fun searchAirports(search: String): Flow<List<Airport>> = dao.searchAirports(search)

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(application.database.dao())
            }
        }
    }

    // uiState for selected origin airport (for flights screen)
        // user selected airport
}

data class SearchUIState(
    var search: String = "",
    var airportList: Flow<List<Airport>> = emptyFlow()
)