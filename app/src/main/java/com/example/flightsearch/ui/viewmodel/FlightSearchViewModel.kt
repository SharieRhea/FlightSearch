package com.example.flightsearch.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Dao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightSearchViewModel(private val dao: Dao) : ViewModel() {
    // uiState for the search bar and search results screen
    private val _uiState by lazy { MutableStateFlow(SearchUIState()) }
    val uiState: StateFlow<SearchUIState> = _uiState

    fun search(search: String) {
        viewModelScope.launch {
            dao.searchAirports(search).collect { results ->
                _uiState.update {
                    it.copy(search = search, airportList = results)
                }
            }
        }
    }

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
    var airportList: List<Airport> = emptyList()
)