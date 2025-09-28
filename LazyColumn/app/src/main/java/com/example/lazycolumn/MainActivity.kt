package com.example.lazycolumn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lazycolumn.ui.theme.LazyColumnTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LazyColumnTheme {
                ContactListScreen()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(modifier: Modifier = Modifier) {
    val contacts: List<String> = remember { generateContacts() }
    val grouped: Map<Char, List<String>> = remember(contacts) {
        contacts.groupBy { it.first() }.toSortedMap()
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val showFab by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 10 }
    }

    Scaffold(
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(index = 0)
                        }
                    }
                ) {
                    Text("Top")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            grouped.forEach { (letter, names) ->
                stickyHeader {
                    Header(letter = letter)
                }
                items(names) { name ->
                    ContactRow(name = name)
                }
            }
        }
    }
}

@Composable
private fun Header(letter: Char) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ContactRow(name: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
        Divider()
    }
}

private fun generateContacts(): List<String> {
    val list = mutableListOf<String>()
    for (c in 'A'..'J') {
        for (i in 1..5) {
            list.add("$c Person $i")
        }
    }
    return list
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContactListPreview() {
    LazyColumnTheme {
        ContactListScreen()
    }
}
