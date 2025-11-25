package com.example.soundmeter

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.log10
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestAudioPermission()

        setContent {
            var soundLevel by remember { mutableStateOf(0f) }

            // 启动录音线程
            LaunchedEffect(Unit) {
                startSoundMeter { dB ->
                    soundLevel = dB
                }
            }

            SoundMeterUI(soundLevel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSoundMeter()
    }


    // ------------------- Permission -------------------
    private fun requestAudioPermission() {
        val perm = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(this, perm)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(perm), 0)
        }
    }


    // ------------------ Sound Meter Logic ------------------

    private fun startSoundMeter(onUpdate: (Float) -> Unit) {
        if (isRecording) return

        val perm = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(this, perm)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        try {
            audioRecord?.startRecording()
        } catch (e: SecurityException) {
            e.printStackTrace()
            return
        }

        isRecording = true

        Thread {
            val buffer = ShortArray(bufferSize)
            while (isRecording) {
                val read = audioRecord!!.read(buffer, 0, bufferSize)

                if (read > 0) {
                    var sum = 0.0
                    for (i in 0 until read) {
                        sum += buffer[i] * buffer[i]
                    }

                    val rms = kotlin.math.sqrt(sum / read)
                    val dB = 20 * kotlin.math.log10(rms)

                    onUpdate(dB.toFloat())
                }
            }
        }.start()
    }

    private fun stopSoundMeter() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}


// -------------------- UI --------------------

@Composable
fun SoundMeterUI(dB: Float) {
    val threshold = 80f
    val clamped = dB.coerceIn(0f, 120f)  // normalize range

    val barColor = when {
        dB < 60 -> Color.Green
        dB < 80 -> Color.Yellow
        else -> Color.Red
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {

        Text(
            text = "Sound Level Meter",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = String.format("Current Level: %.1f dB", dB),
            style = MaterialTheme.typography.headlineSmall
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(clamped / 120f)
                    .background(barColor)
            )
        }

        if (dB > threshold) {
            Text(
                text = "⚠️ Too Loud!",
                color = Color.Red,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
