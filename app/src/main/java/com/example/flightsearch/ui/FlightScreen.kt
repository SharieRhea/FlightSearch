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
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Flight

@Composable
fun FlightResultsScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        SearchBar(modifier = Modifier.fillMaxWidth())
        Text(
            text = "Flights from %s:".format("LAX"),
            style = MaterialTheme.typography.titleMedium
        )
        FlightResultsList()
    }
}

@Composable
fun FlightResultsList(modifier: Modifier = Modifier) {
    LazyColumn {
        items(listOf(
            Flight(
            origin = Airport("LAX", "Los Angeles International Airport"),
            destination = Airport("JFK", "John F. Kennedy International Airport")))
        )
        { flight ->
            FlightCard(
                origin = flight.origin,
                destination = flight.destination,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = "",
        onValueChange = { /* TODO */ },
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
                        text = origin.IATA,
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
                        text = destination.IATA,
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
    FlightResultsScreen(modifier = Modifier.padding(8.dp))
}

@Preview
@Composable
fun FlightCardPreview() {
    FlightCard(
        origin = Airport("LAX", "Los Angeles International Airport"),
        destination = Airport("JFK", "John F. Kennedy International Airport")
    )
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar()
}