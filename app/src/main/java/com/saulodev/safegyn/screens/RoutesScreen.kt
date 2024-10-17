package com.saulodev.safegyn.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class RoutesScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoutesContent()

            }
        }
    }
}

@Composable
fun RoutesContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Rotas de Fuga Seguras", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(20.dp))
        // Aqui você pode usar mapas ou listas com as rotas
        Button(onClick = { /* Calcular rota */ }) {
            Text("Calcular Rota")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoutesPreview() {

        RoutesContent()

}