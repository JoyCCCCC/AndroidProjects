package com.example.colorcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.colorcard.ui.theme.ColorCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColorCardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ColorCard(
                            color = Color(0xFFE57373),
                            label = "Red Card",
                            modifier = Modifier.padding(4.dp)
                        )

                        ColorCard(
                            color = Color(0xFF81C784),
                            label = "Green Card",
                            modifier = Modifier
                                .border(2.dp, Color.Gray)
                                .padding(8.dp)
                        )

                        ColorCard(
                            color = Color(0xFF64B5F6),
                            label = "Blue Card",
                            size = 140.dp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorCard(
    color: Color,
    label: String,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color)
            .border(2.dp, Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(8.dp),
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ColorCardTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ColorCard(color = Color(0xFFE57373), label = "Red Card", modifier = Modifier.padding(4.dp))
            ColorCard(
                color = Color(0xFF81C784),
                label = "Green Card",
                modifier = Modifier.border(2.dp, Color.Gray).padding(8.dp)
            )
            ColorCard(color = Color(0xFF64B5F6), label = "Blue Card", size = 140.dp, modifier = Modifier.padding(4.dp))
        }
    }
}