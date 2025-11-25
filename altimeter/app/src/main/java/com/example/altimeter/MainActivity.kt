package com.example.altimeter

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.random.Random

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null

    private val handler = Handler(Looper.getMainLooper())

    private var pressure by mutableStateOf(1013.25)
    private var altitude by mutableStateOf(0.0)
    private var isSimulating by mutableStateOf(false)
    private var simulatedPressure = 1013.25f

    companion object {
        private const val P0 = 1013.25 // hPa
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        setContent {
            AltimeterApp(
                pressure = pressure,
                altitude = altitude,
                isSimulating = isSimulating,
                onSimulateChange = { enabled ->
                    isSimulating = enabled
                    if (enabled) startSimulation() else stopSimulation()
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        stopSimulation()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_PRESSURE) return

        if (isSimulating) return

        val p = event.values[0].toDouble()
        val h = pressureToAltitude(p)
        pressure = p
        altitude = h
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    // h = 44330 * (1 - (P / P0)^(1 / 5.255))
    private fun pressureToAltitude(p: Double): Double {
        val ratio = p / P0
        return 44330.0 * (1 - ratio.pow(1.0 / 5.255))
    }

    private fun startSimulation() {
        handler.post(simulateRunnable)
    }

    private fun stopSimulation() {
        handler.removeCallbacks(simulateRunnable)
    }

    private val simulateRunnable: Runnable = object : Runnable {
        override fun run() {
            val delta = Random.nextFloat() * 4f - 2f // [-2, 2] hPa  ≈ ±12m
            simulatedPressure += delta
            simulatedPressure = simulatedPressure.coerceIn(800f, 1050f)

            val p = simulatedPressure.toDouble()
            val h = pressureToAltitude(p)
            pressure = p
            altitude = h

            handler.postDelayed(this, 1000L)
        }
    }
}

// ================== Compose UI ==================

@Composable
fun AltimeterApp(
    pressure: Double,
    altitude: Double,
    isSimulating: Boolean,
    onSimulateChange: (Boolean) -> Unit
) {
    val bgColor = altitudeToColor(altitude)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Altimeter",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Text(
                    text = String.format("Pressure: %.2f hPa", pressure),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                Text(
                    text = String.format("Altitude: %.2f m", altitude),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Simulation mode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Switch(
                        checked = isSimulating,
                        onCheckedChange = onSimulateChange
                    )
                }
            }
        }
    }
}

fun altitudeToColor(altitude: Double): Color {
    val maxAlt = 50.0
    val t = (altitude / maxAlt).coerceIn(0.0, 1.0)
    val brightness = 255 - (t * 180).toInt()
    return Color(0, 0, brightness)
}
