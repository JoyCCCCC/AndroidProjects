package com.example.compass

import android.hardware.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.*

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var accelData = FloatArray(3)
    private var magnetData = FloatArray(3)

    private var heading by mutableStateOf(0f)     // compass angle
    private var pitch by mutableStateOf(0f)       // digital level
    private var roll by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        setContent {
            CompassLevelUI(
                heading = heading,
                pitch = pitch,
                roll = roll
            )
        }
    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME
        )

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelData = event.values.clone()

                // pitch / roll (digital level)
                val ax = event.values[0]
                val ay = event.values[1]
                val az = event.values[2]

                pitch = atan2(
                    (-ax).toDouble(),
                    sqrt((ay * ay + az * az).toDouble())
                ).toFloat() * (180f / Math.PI.toFloat())
                roll = atan2(ay.toDouble(), az.toDouble()).toFloat() * (180 / Math.PI.toFloat())
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                magnetData = event.values.clone()
            }
        }

        // Compass orientation using accel + magnet fusion
        val R = FloatArray(9)
        val I = FloatArray(9)
        val success = SensorManager.getRotationMatrix(R, I, accelData, magnetData)

        if (success) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(R, orientation)

            // azimuth is orientation[0]
            heading = (((orientation[0].toDouble() * 180 / Math.PI) + 360) % 360).toFloat()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}


// ===================== COMPOSE UI ==========================

@Composable
fun CompassLevelUI(heading: Float, pitch: Float, roll: Float) {
    Surface(
        Modifier
            .fillMaxSize()
            .background(Color(18, 18, 28)),
        color = Color.Transparent
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                "Compass Heading: ${heading.toInt()}°",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            // Compass needle (Canvas)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CompassNeedle(angle = heading)
            }

            Text(
                text = "Digital Level",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Text(
                text = "Pitch: ${pitch.toInt()}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = "Roll: ${roll.toInt()}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

@Composable
fun CompassNeedle(angle: Float) {
    Canvas(modifier = Modifier.size(200.dp)) {

        rotate(degrees = angle) {
            drawLine(
                color = Color.Red,
                start = Offset(size.width / 2, size.height / 2),
                end = Offset(size.width / 2, 20f),
                strokeWidth = 12f
            )

            drawLine(
                color = Color.White,
                start = Offset(size.width / 2, size.height / 2),
                end = Offset(size.width / 2, size.height - 20f),
                strokeWidth = 12f
            )
        }
    }
}
