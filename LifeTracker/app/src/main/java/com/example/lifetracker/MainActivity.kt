package com.example.lifetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LifeEvent(
    val name: String,
    val timestamp: Long,
    val color: Color
)

fun colorForEvent(name: String): Color = when (name) {
    "onCreate"  -> Color(0xFF6C5CE7)
    "onStart"   -> Color(0xFF0984E3)
    "onResume"  -> Color(0xFF00B894)
    "onPause"   -> Color(0xFFE17055)
    "onStop"    -> Color(0xFFD63031)
    "onDestroy" -> Color(0xFF636E72)
    else        -> Color(0xFFB2BEC3)
}

class LifeTrackerViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<LifeEvent>>(emptyList())
    val events: StateFlow<List<LifeEvent>> = _events

    private val _currentState = MutableStateFlow("Unknown")
    val currentState: StateFlow<String> = _currentState

    private val _snackbarEnabled = MutableStateFlow(true)
    val snackbarEnabled: StateFlow<Boolean> = _snackbarEnabled

    private val _latestEvent = MutableStateFlow<LifeEvent?>(null)
    val latestEvent: StateFlow<LifeEvent?> = _latestEvent

    fun setSnackbarEnabled(enabled: Boolean) {
        _snackbarEnabled.value = enabled
    }

    fun logEvent(name: String) {
        val event = LifeEvent(
            name = name,
            timestamp = System.currentTimeMillis(),
            color = colorForEvent(name)
        )
        _events.value = _events.value + event
        _currentState.value = name
        _latestEvent.value = event
    }
}

class MainActivity : ComponentActivity() {

    private val vm: LifeTrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        vm.logEvent("onCreate")
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                LifeTrackerApp(vm = vm)
            }
        }

        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START    -> vm.logEvent("onStart")
                Lifecycle.Event.ON_RESUME   -> vm.logEvent("onResume")
                Lifecycle.Event.ON_PAUSE    -> vm.logEvent("onPause")
                Lifecycle.Event.ON_STOP     -> vm.logEvent("onStop")
                Lifecycle.Event.ON_DESTROY  -> vm.logEvent("onDestroy")
                else -> Unit
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeTrackerApp(vm: LifeTrackerViewModel) {
    val events by vm.events.collectAsState()
    val current by vm.currentState.collectAsState()
    val snackbarEnabled by vm.snackbarEnabled.collectAsState()
    val latestEvent by vm.latestEvent.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(latestEvent, snackbarEnabled) {
        val e = latestEvent
        if (snackbarEnabled && e != null) {
            snackbarHostState.showSnackbar(
                message = "Lifecycle changed: ${e.name}",
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LifeTracker") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text("Snackbar", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = snackbarEnabled,
                            onCheckedChange = { vm.setSnackbarEnabled(it) }
                        )
                    }
                }
            )

        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            CurrentStateCard(currentState = current)

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Lifecycle Event Logs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(8.dp))

            EventList(events = events)
        }
    }
}

@Composable
fun CurrentStateCard(currentState: String) {
    val color = colorForEvent(currentState)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(14.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(color)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Current State", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = currentState,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
fun EventList(events: List<LifeEvent>) {
    if (events.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No events yet…")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(events) { index, item ->
            EventRow(index = index + 1, event = item)
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun EventRow(index: Int, event: LifeEvent) {
    val timeText = remember(event.timestamp) {
        val fmt = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        fmt.format(Date(event.timestamp))
    }
    Card {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(10.dp, 24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(event.color)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "$index. ${event.name}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = "Time: $timeText",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
