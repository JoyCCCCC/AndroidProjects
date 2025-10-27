package com.example.counterpp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CounterUiState(
    val count: Int = 0,
    val auto: Boolean = false,
    val intervalSec: Int = 3
)

class CounterViewModel : ViewModel() {
    private val _state = MutableStateFlow(CounterUiState())
    val state: StateFlow<CounterUiState> = _state

    private var autoJob: Job? = null

    fun inc() = _state.update { it.copy(count = it.count + 1) }
    fun dec() = _state.update { it.copy(count = it.count - 1) }
    fun reset() = _state.update { it.copy(count = 0) }

    fun toggleAuto() {
        val newAuto = !_state.value.auto
        _state.update { it.copy(auto = newAuto) }
        restartAutoJobIfNeeded()
    }

    fun setInterval(seconds: Int) {
        val sec = seconds.coerceIn(1, 10)
        _state.update { it.copy(intervalSec = sec) }
        if (_state.value.auto) restartAutoJobIfNeeded()
    }

    private fun restartAutoJobIfNeeded() {
        autoJob?.cancel()
        autoJob = null

        if (_state.value.auto) {
            autoJob = viewModelScope.launch {
                while (true) {
                    delay(_state.value.intervalSec * 1000L)
                    _state.update { it.copy(count = it.count + 1) }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoJob?.cancel()
    }
}

enum class Screen { Main, Settings }

class MainActivity : ComponentActivity() {
    private val vm: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                var screen by remember { mutableStateOf(Screen.Main) }

                when (screen) {
                    Screen.Main -> CounterScreen(
                        vm = vm,
                        onOpenSettings = { screen = Screen.Settings }
                    )
                    Screen.Settings -> SettingsScreen(
                        vm = vm,
                        onBack = { screen = Screen.Main }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(vm: CounterViewModel, onOpenSettings: () -> Unit) {
    val ui by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Counter++") },
                actions = {
                    TextButton(onClick = onOpenSettings) { Text("Settings") }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Count",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = ui.count.toString(),
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Auto mode: " + if (ui.auto) "ON" else "OFF",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Interval: ${ui.intervalSec}s",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { vm.inc() }) { Text("+1") }
                Button(onClick = { vm.dec() }) { Text("-1") }
                OutlinedButton(onClick = { vm.reset() }) { Text("Reset") }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Auto")
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = ui.auto,
                    onCheckedChange = { vm.toggleAuto() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: CounterViewModel, onBack: () -> Unit) {
    val ui by vm.state.collectAsState()
    var text by remember(ui.intervalSec) { mutableStateOf(ui.intervalSec.toString()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Auto-increment interval (seconds)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { new ->
                    text = new.filter { it.isDigit() }.take(2)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(180.dp),
                placeholder = { Text("1 ~ 10") }
            )

            Spacer(Modifier.height(12.dp))
            Text("Range: 1–10 seconds", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(20.dp))
            Button(onClick = {
                val value = text.toIntOrNull()
                if (value == null) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Please enter a valid number")
                    }
                } else {
                    vm.setInterval(value)
                    scope.launch {
                        snackbarHostState.showSnackbar("Saved: ${value.coerceIn(1, 10)}s")
                    }
                }
            }) {
                Text("Save")
            }
        }
    }
}
