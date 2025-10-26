package com.udb.controlgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.udb.controlgastos.ui.screens.HomeScreen
import com.udb.controlgastos.ui.screens.LoginScreen
import com.udb.controlgastos.ui.theme.ControlGastosTheme
import com.udb.controlgastos.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControlGastosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ControlGastosApp()
                }
            }
        }
    }
}

@Composable
fun ControlGastosApp(viewModel: AuthViewModel = viewModel()) {
    var isAuthenticated by remember { mutableStateOf(false) }
    val currentUser by viewModel.currentUser.collectAsState()

    // Estado de autenticación
    LaunchedEffect(currentUser) {
        isAuthenticated = currentUser != null
    }

    if (isAuthenticated) {
        HomeScreen(
            onSignOut = {
                isAuthenticated = false
            },
            viewModel = viewModel
        )
    } else {
        LoginScreen(
            onNavigateToHome = {
                isAuthenticated = true
            },
            viewModel = viewModel
        )
    }
}