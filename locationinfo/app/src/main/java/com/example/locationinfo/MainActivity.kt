package com.example.locationinfo

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var userLocation by mutableStateOf<LatLng?>(null)
    private var addressText by mutableStateOf("Requesting location...")
    private var hasLocationPermission by mutableStateOf(false)
    private var isShowingSelectedAddress by mutableStateOf(false)


    companion object {
        private const val LOCATION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val latLng = LatLng(location.latitude, location.longitude)
                userLocation = latLng
                if (!isShowingSelectedAddress) {
                    updateAddress(latLng)
                }
            }
        }

        checkLocationPermission()

        setContent {
            LocationMapScreen(
                userLocation = userLocation,
                addressText = addressText,
                hasLocationPermission = hasLocationPermission,
                onMapTap = { latLng ->
                    isShowingSelectedAddress = true
                    updateAddress(latLng)
                }
            )
        }
    }

    private fun checkLocationPermission() {
        val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, fineLocation)
            == PackageManager.PERMISSION_GRANTED
        ) {
            hasLocationPermission = true
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(fineLocation),
                LOCATION_REQUEST_CODE
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                hasLocationPermission = true
                startLocationUpdates()
            } else {
                hasLocationPermission = false
                addressText = "Location permission denied"
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startLocationUpdates() {
        if (!hasLocationPermission) return

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                userLocation = latLng
                updateAddress(latLng)
            } else {
                addressText = "Could not get last known location"
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private fun updateAddress(latLng: LatLng) {
        try {
            val results: List<Address>? =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addressText = if (!results.isNullOrEmpty()) {
                val addr = results[0]
                buildString {
                    for (i in 0..addr.maxAddressLineIndex) {
                        append(addr.getAddressLine(i))
                        if (i != addr.maxAddressLineIndex) append("\n")
                    }
                }
            } else {
                "No address found for this location"
            }
        } catch (e: Exception) {
            addressText = "Geocoder error: ${e.message}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (hasLocationPermission) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

// =================== Compose UI ===================

@Composable
fun LocationMapScreen(
    userLocation: LatLng?,
    addressText: String,
    hasLocationPermission: Boolean,
    onMapTap: (LatLng) -> Unit
) {
    val defaultLatLng = userLocation ?: LatLng(0.0, 0.0)
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 1f)
    }

    val markers = remember { mutableStateListOf<LatLng>() }

    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(loc, 15f)
            )
            if (!markers.contains(loc)) {
                markers.add(loc)
            }
        }
    }

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
                        isMyLocationEnabled = hasLocationPermission
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true
                    ),
                    onMapClick = { latLng ->
                        markers.add(latLng)
                        onMapTap(latLng)
                    }
                ) {
                    markers.forEach { point ->
                        Marker(
                            state = MarkerState(position = point),
                            title = if (userLocation != null && point == userLocation)
                                "You are here"
                            else
                                "Custom marker"
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                Text(
                    text = addressText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        }
    }
}
