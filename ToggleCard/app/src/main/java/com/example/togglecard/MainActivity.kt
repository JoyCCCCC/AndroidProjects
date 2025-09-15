package com.example.togglecard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.togglecard.ui.theme.ToggleCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToggleCardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ToggleCard()
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleCard(modifier: Modifier = Modifier) {
    val toggledState = rememberSaveable { mutableStateOf(false) }
    val message = if (toggledState.value) {
        "Kotlin was created by JetBrains!"
    } else {
        "Tap to see a fun fact!"
    }

    Box(
        modifier = modifier
            .size(220.dp)
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color.LightGray)
            .clickable { toggledState.value = !toggledState.value },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToggleCardPreview() {
    ToggleCardTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToggleCard()
        }
    }
}