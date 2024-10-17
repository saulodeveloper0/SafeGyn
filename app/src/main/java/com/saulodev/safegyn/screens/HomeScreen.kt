package com.saulodev.safegyn.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Marker
import androidx.preference.PreferenceManager
import java.io.File

@SuppressLint("MissingPermission")
@Composable
fun HomeContent(fusedLocationClient: FusedLocationProviderClient) {
    val context = LocalContext.current

    // Variáveis de estado
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationPermissionGranted by remember { mutableStateOf(false) }

    // Solicitar permissões de localização
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted = isGranted
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    // Verifica permissões
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationPermissionGranted = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    // Estrutura principal com Scaffold
    Scaffold(
        topBar = { TopAppBarModern() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Adicionar ação aqui */ },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Mapa interativo com a simulação
                MapSection(
                    userLocation = userLocation,
                    locationPermissionGranted = locationPermissionGranted
                )

                // Botões flutuantes
                FooterButtons(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarModern() {
    TopAppBar(
        title = {
            Text(
                "Simulação de Rompimento da Barragem",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
    )
}

@Composable
fun FooterButtons(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botão 1 - Rotas de Fuga
        ElevatedButton(
            onClick = { /* Navegar para rotas de fuga */ },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
                .height(60.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Rotas de Fuga", style = MaterialTheme.typography.bodyLarge)
        }

        // Botão 2 - Notificações
        ElevatedButton(
            onClick = { /* Abrir notificações */ },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
                .height(60.dp)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Notificações", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun MapSection(userLocation: LatLng?, locationPermissionGranted: Boolean) {
    val context = LocalContext.current
    var markerLocation by remember { mutableStateOf<GeoPoint?>(null) }

    // Inicialização do OSMdroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.cacheDir.also {
                Configuration.getInstance().osmdroidBasePath = it
                Configuration.getInstance().osmdroidTileCache = File(it, "osmdroid/tiles")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .background(Color.LightGray)
    ) {
        AndroidView(factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                val mapController = this.controller

                // Centraliza a vista em Goiânia
                val goianiaPoint = GeoPoint(-16.6869, -49.2648)
                mapController.setCenter(goianiaPoint)
                mapController.setZoom(11.0)  // Zoom ajustado para cobrir uma área maior da cidade

                // Polígonos para toda a cidade de Goiânia

                // Área 1 - Região baixa
                val lowArea1 = Polygon().apply {
                    points = listOf(
                        GeoPoint(-16.7600, -49.3400),  // Ponto 1
                        GeoPoint(-16.7800, -49.2900),  // Ponto 2
                        GeoPoint(-16.7500, -49.2500),  // Ponto 3
                        GeoPoint(-16.7200, -49.2700),  // Ponto 4
                        GeoPoint(-16.7000, -49.3000)   // Fechar o polígono
                    )
                    fillPaint.color = AndroidColor.argb(128, 0, 255, 0)  // Verde semi-transparente
                    strokeColor = AndroidColor.GREEN
                    strokeWidth = 3f
                }

                // Área 2 - Região intermediária
                val midArea2 = Polygon().apply {
                    points = listOf(
                        GeoPoint(-16.7200, -49.2600),  // Ponto 1
                        GeoPoint(-16.7000, -49.2400),  // Ponto 2
                        GeoPoint(-16.6700, -49.2300),  // Ponto 3
                        GeoPoint(-16.6600, -49.2600),  // Ponto 4
                        GeoPoint(-16.6800, -49.2800)   // Fechar o polígono
                    )
                    fillPaint.color = AndroidColor.argb(128, 255, 255, 0)  // Amarelo semi-transparente
                    strokeColor = AndroidColor.YELLOW
                    strokeWidth = 3f
                }

                // Área 3 - Região alta
                val highArea3 = Polygon().apply {
                    points = listOf(
                        GeoPoint(-16.6700, -49.2100),  // Ponto 1
                        GeoPoint(-16.6500, -49.2200),  // Ponto 2
                        GeoPoint(-16.6300, -49.2300),  // Ponto 3
                        GeoPoint(-16.6100, -49.2100),  // Ponto 4
                        GeoPoint(-16.6200, -49.1900)   // Fechar o polígono
                    )
                    fillPaint.color = AndroidColor.argb(128, 255, 0, 0)  // Vermelho semi-transparente
                    strokeColor = AndroidColor.RED
                    strokeWidth = 3f
                }

                // Mais áreas podem ser adicionadas conforme necessário...

                // Adicionar os polígonos ao mapa
                this.overlays.add(lowArea1)
                this.overlays.add(midArea2)
                this.overlays.add(highArea3)

                // Marcador de localização do usuário (se disponível)
                if (locationPermissionGranted && userLocation != null) {
                    val userPoint = GeoPoint(userLocation.latitude, userLocation.longitude)
                    mapController.setCenter(userPoint)
                    mapController.setZoom(16.0)

                    val userMarker = Marker(this).apply {
                        position = userPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Sua Localização"
                    }
                    this.overlays.add(userMarker)
                }
            }
        },
            modifier = Modifier.fillMaxSize()
        )
    }
}
