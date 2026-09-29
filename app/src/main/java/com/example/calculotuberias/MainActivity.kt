package com.example.calculotuberias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.calculotuberias.navigation.GrafoNavegacion
import com.example.calculotuberias.ui.theme.HidroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // La versión anterior llamaba a AppNavigation() fuera del tema, así que
            // MaterialTheme caía a sus valores por defecto morados.
            HidroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    GrafoNavegacion()
                }
            }
        }
    }
}
