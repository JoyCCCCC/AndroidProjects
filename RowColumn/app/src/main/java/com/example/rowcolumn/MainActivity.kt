package com.example.rowcolumn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rowcolumn.ui.theme.RowColumnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RowColumnTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LayoutDemo(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LayoutDemo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()
                .background(Color(0xFF81D4FA)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "25%")
        }

        Column(
            modifier = Modifier
                .weight(0.75f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .background(Color(0xFFA5D6A7)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Top (2/10)")
            }

            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .background(Color(0xFFFFF59D)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Middle (3/10)")
            }

            Box(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxWidth()
                    .background(Color(0xFFEF9A9A)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Bottom (5/10)")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LayoutDemoPreview() {
    RowColumnTheme {
        LayoutDemo()
    }
}
