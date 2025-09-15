package com.example.kotlinpracticescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlinpracticescreen.ui.theme.KotlinPracticeScreenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinPracticeScreenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KotlinPracticeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun KotlinPracticeScreen(modifier: Modifier = Modifier) {
    var counter by remember { mutableStateOf(0) }

    val input = "dog"

    val animalSound = when (input) {
        "cat" -> "Meow"
        "dog" -> "Woof"
        "fish" -> "Blub"
        else -> "Unknown animal"
    }

    val nullableMessage: String? = "This string is not null!"

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Animal: $input → $animalSound")

        nullableMessage?.let {
            Text(text = "Message: $it")
        }

        Text(text = "Counter: $counter")

        Button(onClick = {
            if (counter < 5) counter++
        }) {
            Text("Increment Counter")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KotlinPracticeScreenPreview() {
    KotlinPracticeScreenTheme {
        KotlinPracticeScreen()
    }
}
