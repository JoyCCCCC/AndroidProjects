package com.example.trailmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrailMapScreen()
        }
    }
}

@Composable
fun TrailMapScreen() {
    val trailPoints = remember {
        listOf(
            LatLng(37.4219999, -122.0840575),  // A
            LatLng(37.4235, -122.0865),        // B
            LatLng(37.4250, -122.0880),        // C
            LatLng(37.4265, -122.0900)         // D
        )
    }

    val parkPolygonPoints = remember {
        listOf(
            LatLng(37.4275, -122.0915),
            LatLng(37.4270, -122.0880),
            LatLng(37.4290, -122.0870),
            LatLng(37.4300, -122.0905)
        )
    }

    val initialCenter = trailPoints.first()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialCenter, 15f)
    }

    var polylineColor by remember { mutableStateOf(Color(0xFF1E90FF)) } // DodgerBlue
    var polylineWidth by remember { mutableStateOf(10f) }

    var polygonStrokeColor by remember { mutableStateOf(Color(0xFF2ECC71)) } // Green
    var polygonFillColor by remember { mutableStateOf(Color(0x552ECC71)) }
    var polygonStrokeWidth by remember { mutableStateOf(6f) }

    var infoText by remember { mutableStateOf("Tap the trail or park to see info.") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101020)),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = MapType.TERRAIN
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true
                    ),
                    onMapLoaded = {
                        cameraPositionState.move(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(initialCenter, 15f)
                            )
                        )
                    }
                ) {
                    // Hiking Trail
                    Polyline(
                        points = trailPoints,
                        color = polylineColor,
                        width = polylineWidth,
                        clickable = true,
                        onClick = {
                            infoText = "Trail: Mountain Loop Trail\nApprox. length: ~3.5 km\nDifficulty: Moderate"
                        }
                    )

                    // Park
                    Polygon(
                        points = parkPolygonPoints,
                        fillColor = polygonFillColor,
                        strokeColor = polygonStrokeColor,
                        strokeWidth = polygonStrokeWidth,
                        clickable = true,
                        onClick = {
                            infoText = "Park: Green Valley Park\nOpen: 6:00 - 22:00\nFacilities: Picnic area, restrooms, viewpoints"
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Trail & Park Style Controls",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Trail Color:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { polylineColor = Color(0xFF1E90FF) }) {
                        Text("Blue")
                    }
                    Button(onClick = { polylineColor = Color.Red }) {
                        Text("Red")
                    }
                    Button(onClick = { polylineColor = Color.Magenta }) {
                        Text("Magenta")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Trail Width: ${polylineWidth.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Slider(
                    value = polylineWidth,
                    onValueChange = { polylineWidth = it },
                    valueRange = 4f..24f
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Park Border Width: ${polygonStrokeWidth.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Slider(
                    value = polygonStrokeWidth,
                    onValueChange = { polygonStrokeWidth = it },
                    valueRange = 2f..20f
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Info:",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )
                Text(
                    text = infoText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
