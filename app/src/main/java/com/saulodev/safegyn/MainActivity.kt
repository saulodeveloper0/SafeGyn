package com.saulodev.safegyn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.saulodev.safegyn.screens.HomeContent
import com.saulodev.safegyn.ui.theme.SafeGynTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxSize

class MainActivity : ComponentActivity() {

    // Inicializa o FusedLocationProviderClient para capturar a localização do usuário
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializando o FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            SafeGynTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Passa o FusedLocationProviderClient para o HomeContent

                    HomeContent(fusedLocationClient = fusedLocationClient)
                }
            }
        }
    }
}
