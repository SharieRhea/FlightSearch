package com.example.flightsearch.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Flight
import com.example.flightsearch.ui.viewmodel.FlightSearchViewModel

@Composable
fun FlightSearchApp(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory)
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle(lifecycleOwner)

    Column(
        modifier = modifier
    ) {
        SearchBar(
            getSearchContent = viewModel::getSearchContent,
            updateUiStateSearch = viewModel::search,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )

        val departingAirport = uiState.value.departingAirport
        Log.d("PREFERENCES", "uiState departingAirport: $departingAirport")
        Log.d("PREFERENCES", "uiState Search: ${uiState.value.searchString}")
        if (departingAirport == null && uiState.value.searchString.isEmpty()) {
            Text(
                text = "Favorite Routes:",
                modifier = Modifier.padding(8.dp)
            )
            FlightResultsList(
                flightsList = uiState.value.favoritesList,
                onClickFavorite = viewModel::toggleFavorite,
                modifier = Modifier.padding(8.dp)
            )
        } else if (departingAirport == null) {
            SearchResultsScreen(
                airportsList = uiState.value.airportsList,
                updateDepartingAirport = viewModel::updateDepartingAirport,
            )
        } else {
            FlightResultsScreen(
                departingAirport = departingAirport,
                flightsList = uiState.value.flightsList,
                onClickFavorite = viewModel::toggleFavorite,
            )
        }
    }
}

@Composable
fun SearchResultsScreen(
    airportsList: List<Airport>,
    updateDepartingAirport: (Airport) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        AirportResultsList(
            airportsList = airportsList,
            updateDepartingAirport = updateDepartingAirport,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FlightResultsScreen(
    departingAirport: Airport,
    flightsList: List<Flight>,
    onClickFavorite: (flight: Flight) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Flights from %s:".format(departingAirport.name),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
        FlightResultsList(
            flightsList = flightsList,
            onClickFavorite = onClickFavorite,
            modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun FlightResultsList(
    flightsList: List<Flight>,
    onClickFavorite: (flight: Flight) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(flightsList) { flight ->
            FlightCard(
                flight = flight,
                onClickFavorite = onClickFavorite,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AirportResultsList(
    airportsList: List<Airport>,
    updateDepartingAirport: (Airport) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy((-16).dp),
        modifier = modifier
    ) {
        items(airportsList) { airport ->
            AirportCard(
                airport = airport,
                updateDepartingAirport = updateDepartingAirport,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirportCard(
    airport: Airport,
    updateDepartingAirport: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = { updateDepartingAirport(airport) },
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = airport.iata,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = airport.name,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SearchBar(
    getSearchContent: () -> String,
    updateUiStateSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var search by remember { mutableStateOf(getSearchContent())}
    OutlinedTextField(
        value = search,
        onValueChange = {
            search = it
            updateUiStateSearch(it)
        },
        placeholder = { Text(text = stringResource(R.string.search_placeholder))},
        leadingIcon = { Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null
        )},
        modifier = modifier
    )
}

@Composable
fun FlightCard(
    flight: Flight,
    onClickFavorite: (flight: Flight) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.9f)
            ) {
                Text(
                    text = stringResource(id = R.string.depart_label),
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = flight.origin.iata,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = flight.origin.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = stringResource(id = R.string.arrive_label),
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = flight.destination.iata,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = flight.destination.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            val enabledColor: Color = MaterialTheme.colorScheme.primary
            var iconColor: Color by remember { mutableStateOf(if (flight.favorite) enabledColor else Color.Gray) }
            IconButton(
                onClick = {
                    onClickFavorite(flight)
                    iconColor = if (iconColor == Color.Gray) enabledColor else Color.Gray
                },
                modifier = Modifier.weight(0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    tint = iconColor,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SearchResultsScreenPreview() {
    SearchResultsScreen(
        airportsList = listOf(
            Airport(id = 0, iata = "LAX", name = "Los Angeles International Airport", passengers = 42069),
            Airport(id = 1, iata = "MUC", name = "Munich International Airport", passengers = 47959885),
            Airport(id = 1, iata = "MUC", name = "Munich International Airport", passengers = 47959885),
            Airport(id = 1, iata = "MUC", name = "Munich International Airport", passengers = 47959885),
            Airport(id = 1, iata = "MUC", name = "Munich International Airport", passengers = 47959885)
        ),
        updateDepartingAirport = {},
        modifier = Modifier.padding(8.dp))
}

@Preview(showSystemUi = true)
@Composable
fun FlightResultsScreenPreview() {
    FlightResultsScreen(
        departingAirport = Airport(id = 1, iata = "MUC", name = "Munich International Airport", passengers = 47959885),
        flightsList = listOf(
            Flight(
                Airport(id = 0, iata = "LAX", name = "Los Angeles International Airport", passengers = 42069),
                Airport(id = 1, iata = "MUC", name = "Sheremetyevo - A.S. Pushkin international airport", passengers = 47959885)
            ),
            Flight(
                Airport(id = 0, iata = "LAX", name = "Los Angeles International Airport", passengers = 42069),
                Airport(id = 1, iata = "MUC", name = "Munich International Airport", passengers = 47959885)
            ),
            Flight(
                Airport(id = 0, iata = "LAX", name = "Los Angeles International Airport", passengers = 42069),
                Airport(id = 1, iata = "MUC", name = "Munich International Airport", passengers = 47959885)
            )
        ),
        onClickFavorite = {})
}

@Preview
@Composable
fun AirportResultPreview() {
    AirportCard(
        airport = Airport(id = 0, iata = "LAX", name = "Los Angeles International Airport", passengers = 42069),
        updateDepartingAirport = {}
    )
}

@Preview
@Composable
fun FlightCardPreview() {
    FlightCard(
        flight = Flight(Airport(id = 0, iata = "LAX", name = "Los Angeles International Airport", passengers = 42069),
        destination = Airport(id = 1, iata = "MUC", name = "Munich Internation Airport", passengers = 47959885)),
        onClickFavorite = {}
    )
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(updateUiStateSearch = {}, getSearchContent = { "" } )

}