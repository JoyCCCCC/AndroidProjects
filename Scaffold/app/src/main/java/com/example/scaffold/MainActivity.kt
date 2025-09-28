package com.example.scaffold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.scaffold.ui.theme.ScaffoldTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScaffoldTheme {
                AppScaffold()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppScaffold() {
    var selectedIndex by remember { mutableStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Scaffold Demo") })
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    "Home" to "🏠",
                    "Settings" to "⚙️",
                    "Profile" to "👤"
                )
                items.forEachIndexed { index, (label, emoji) ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Text(emoji) },
                        label = { Text(label) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("FAB clicked on ${tabName(selectedIndex)}")
                    }
                }
            ) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        ContentArea(
            selectedIndex = selectedIndex,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
private fun ContentArea(selectedIndex: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Current tab: ${tabName(selectedIndex)}",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

private fun tabName(index: Int): String = when (index) {
    0 -> "Home"
    1 -> "Settings"
    2 -> "Profile"
    else -> "Home"
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppScaffoldPreview() {
    ScaffoldTheme {
        AppScaffold()
    }
}
