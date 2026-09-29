package com.example.calculotuberias.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.outlined.Water
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.calculotuberias.R
import com.example.calculotuberias.ui.components.ListaOpciones
import com.example.calculotuberias.ui.components.OpcionMenu
import com.example.calculotuberias.ui.components.PantallaCalculo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    irAConductos: () -> Unit,
    irACanales: () -> Unit,
    irAAcercaDe: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculadora Hidráulica") },
                actions = {
                    IconButton(onClick = irAAcercaDe) {
                        Icon(Icons.Default.Info, contentDescription = "Documentación")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "HidroFES Flow",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text(
                        "Cálculo de conductos a presión y canales a superficie libre",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                    )
                }
            }

            TarjetaAcceso(
                titulo = "Conductos a presión",
                descripcion = "Caudal, diámetro, pérdidas de carga y golpe de ariete",
                onClick = irAConductos,
            )

            TarjetaAcceso(
                titulo = "Canales",
                descripcion = "Flujo uniforme por Manning en cuatro geometrías",
                onClick = irACanales,
            )

            Spacer(Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.cintillo),
                contentDescription = "UNAM FES Aragón",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaAcceso(titulo: String, descripcion: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.Water,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp),
            )
            Spacer(Modifier.size(20.dp))
            Column {
                Text(titulo, style = MaterialTheme.typography.titleLarge)
                Text(
                    descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun MenuConductosScreen(
    onVolver: () -> Unit,
    irACaudal: () -> Unit,
    irADiametro: () -> Unit,
    irAPerdidas: () -> Unit,
    irACeleridad: () -> Unit,
) {
    PantallaCalculo(
        titulo = "Conductos a presión",
        onVolver = onVolver,
    ) { innerPadding ->
        ListaOpciones(
            innerPadding = innerPadding,
            opciones = listOf(
                OpcionMenu(
                    "Caudal",
                    "Gasto que circula por un diámetro conocido bajo una carga disponible",
                    Icons.Default.Waves,
                    irACaudal,
                ),
                OpcionMenu(
                    "Diámetro",
                    "Diámetro necesario para conducir un gasto por gravedad",
                    Icons.Default.Straighten,
                    irADiametro,
                ),
                OpcionMenu(
                    "Pérdidas de carga",
                    "Por fricción (Darcy, Hazen-Williams, Manning) y por accesorios",
                    Icons.AutoMirrored.Filled.TrendingDown,
                    irAPerdidas,
                ),
                OpcionMenu(
                    "Celeridad",
                    "Velocidad de la onda de presión y golpe de ariete",
                    Icons.Default.Bolt,
                    irACeleridad,
                ),
            ),
        )
    }
}

@Composable
fun MenuPerdidasScreen(
    onVolver: () -> Unit,
    irAFriccion: () -> Unit,
    irALocales: () -> Unit,
) {
    PantallaCalculo(
        titulo = "Pérdidas de carga",
        onVolver = onVolver,
    ) { innerPadding ->
        ListaOpciones(
            innerPadding = innerPadding,
            opciones = listOf(
                OpcionMenu(
                    "Por fricción",
                    "Pérdidas primarias a lo largo de la conducción",
                    Icons.AutoMirrored.Filled.TrendingDown,
                    irAFriccion,
                ),
                OpcionMenu(
                    "Locales o menores",
                    "Pérdidas secundarias por válvulas, codos y piezas especiales",
                    Icons.Default.Calculate,
                    irALocales,
                ),
            ),
        )
    }
}

