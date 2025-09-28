package com.example.themedform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.themedform.ui.theme.ThemedFormTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemedFormTheme {
                AppScaffold()
            }
        }
    }
}

@Composable
fun AppScaffold() {
    Scaffold { innerPadding ->
        LoginForm(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        )
    }
}

@Composable
fun LoginForm(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun submit() {
        usernameError = if (username.isBlank()) "Username cannot be empty" else null
        passwordError = if (password.isBlank()) "Password cannot be empty" else null
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            isError = usernameError != null,
            supportingText = {
                if (usernameError != null) {
                    Text(usernameError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { submit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginFormPreview() {
    ThemedFormTheme {
        AppScaffold()
    }
}
