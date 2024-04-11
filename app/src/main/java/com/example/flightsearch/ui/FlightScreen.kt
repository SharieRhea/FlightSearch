package com.example.flightsearch.ui

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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.ui.viewmodel.FlightSearchViewModel

@Composable
fun SearchResultsScreen(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory)
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        val lifecycleOwner = LocalLifecycleOwner.current
        val uiState = viewModel.uiState.collectAsStateWithLifecycle(lifecycleOwner)
        AirportResultsList(uiState.value.airportList)
    }
}

@Composable
fun FlightResultsScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Flights from %s:".format("LAX"),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun FlightResultsList(flightsList: List<Pair<Airport, Airport>>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        items(flightsList
        )
        { flightList ->
            FlightCard(
                origin = flightList.first,
                destination = flightList.second,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AirportResultsList(
    airportsList: List<Airport>,
    modifier: Modifier = Modifier,
) {
    //val airportsList by viewModel.searchAirports(viewModel.uiState.collectAsState().value.search).collectAsState(initial = emptyList())
    LazyColumn(
        modifier = modifier
    ) {
        items(airportsList
        )
        { airport ->
            AirportCard(airport = airport)
        }
    }
}

@Composable
fun AirportCard(airport: Airport, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
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

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory)
) {
    var search by remember { mutableStateOf("")}
    OutlinedTextField(
        value = search,
        onValueChange = {
            viewModel.search(it)
            search = it
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
fun FlightCard(origin: Airport, destination: Airport, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.depart_label),
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = origin.iata,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = origin.name,
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
                        text = destination.iata,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = destination.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            IconButton(
                onClick = { /*TODO*/ },
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun FlightResultsScreenPreview() {
    SearchResultsScreen(modifier = Modifier.padding(8.dp))
}

@Preview
@Composable
fun FlightCardPreview() {
    //FlightCard()
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar()
}