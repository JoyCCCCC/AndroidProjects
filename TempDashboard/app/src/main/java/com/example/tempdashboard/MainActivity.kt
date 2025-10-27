package com.example.tempdashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class TempReading(val timestamp: Long, val value: Float)

data class DashboardState(
    val readings: List<TempReading> = emptyList(),
    val paused: Boolean = false
) {
    val current: Float? get() = readings.lastOrNull()?.value
    val avg: Float? get() = if (readings.isEmpty()) null else readings.map { it.value }.average().toFloat()
    val min: Float? get() = readings.minByOrNull { it.value }?.value
    val max: Float? get() = readings.maxByOrNull { it.value }?.value
}

class TempViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state
    private var job: Job? = null

    init {
        start()
    }

    fun togglePause() {
        val newPaused = !_state.value.paused
        _state.update { it.copy(paused = newPaused) }
        if (newPaused) stop() else start()
    }

    fun setPaused(p: Boolean) {
        _state.update { it.copy(paused = p) }
        if (p) stop() else start()
    }

    private fun start() {
        if (job != null) return
        job = viewModelScope.launch {
            while (true) {
                delay(2000)
                if (!_state.value.paused) {
                    val v = Random.nextFloat() * (85f - 65f) + 65f
                    addReading(TempReading(System.currentTimeMillis(), v))
                }
            }
        }
    }

    private fun stop() {
        job?.cancel()
        job = null
    }

    private fun addReading(r: TempReading) {
        _state.update { s ->
            val next = (s.readings + r).takeLast(20)
            s.copy(readings = next)
        }
    }
}

class MainActivity : ComponentActivity() {
    private val vm: TempViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                DashboardApp(vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardApp(vm: TempViewModel) {
    val ui by vm.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Temperature Dashboard") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (ui.paused) "Paused" else "Running")
                        Spacer(Modifier.width(8.dp))
                        Switch(checked = !ui.paused, onCheckedChange = { vm.setPaused(!ui.paused) })
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            StatsRow(ui)
            Spacer(Modifier.height(12.dp))
            Chart(ui.readings, Modifier.fillMaxWidth().height(160.dp))
            Spacer(Modifier.height(12.dp))
            Text("Readings", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(8.dp))
            ReadingsList(ui.readings, Modifier.weight(1f))
        }
    }
}

@Composable
fun StatsRow(ui: DashboardState) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Current", ui.current?.let { "%.1f°F".format(it) } ?: "--", Modifier.weight(1f))
        StatCard("Average", ui.avg?.let { "%.1f°F".format(it) } ?: "--", Modifier.weight(1f))
        StatCard("Min", ui.min?.let { "%.1f°F".format(it) } ?: "--", Modifier.weight(1f))
        StatCard("Max", ui.max?.let { "%.1f°F".format(it) } ?: "--", Modifier.weight(1f))
    }
}


@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}


@Composable
fun Chart(readings: List<TempReading>, modifier: Modifier = Modifier) {
    if (readings.size < 2) {
        Box(modifier, contentAlignment = Alignment.Center) { Text("Not enough data") }
        return
    }
    val minV = readings.minOf { it.value }
    val maxV = readings.maxOf { it.value }
    val span = max(0.1f, maxV - minV)
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val n = readings.size
        val dx = if (n <= 1) 0f else w / (n - 1).toFloat()
        var prev: Offset? = null
        readings.forEachIndexed { i, r ->
            val x = i * dx
            val y = h - ((r.value - minV) / span) * h
            val p = Offset(x, y)
            prev?.let { drawLine(color = lineColor, start = it, end = p, strokeWidth = 4f) }
            prev = p
        }
    }
}


@Composable
fun ReadingsList(readings: List<TempReading>, modifier: Modifier = Modifier) {
    val fmt = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    LazyColumn(modifier) {
        itemsIndexed(readings) { idx, item ->
            Card {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${idx + 1}.", modifier = Modifier.width(28.dp))
                    Text(fmt.format(Date(item.timestamp)), modifier = Modifier.width(80.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("%.1f°F".format(item.value), style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}
