package com.example.calculotuberias.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.calculotuberias.R
import com.example.calculotuberias.navigation.AppScreens
import com.example.calculotuberias.navigation.BottomNavBar
import com.example.calculotuberias.navigation.NavListMenu
import kotlin.math.*

@Composable
fun CaudalScreen(navController: NavHostController) {
    var selectedIndex by remember { mutableIntStateOf(2) }

    val materiales = mapOf(
        "Nada" to 0.0,
        "Acero Esmaltado" to 0.0000048,
        "Acero Galvanizado" to 0.00015,
        "Acero Nuevo" to 0.000045,
        "Acero Remachado" to 0.0009,
        "Asbesto Cemento" to 0.00003,
        "Cobre" to 0.0000015,
        "Concreto (Cimbra Madera)" to 0.0006,
        "Concreto (Cimbra Metálica)" to 0.00018,
        "Concreto Simple" to 0.00036,
        "Policloruro de Vinilo Clorado" to 0.0000015,
        "Hierro Dúctil" to 0.00025,
        "Hierro Fundido" to 0.00015,
        "Hierro Fundido Asfaltado" to 0.00012,
        "Hierro Galvanizado" to 0.00015,
        "Latón" to 0.0000015,
        "Madera Duelas" to 0.00018,
        "Polietileno de Alta Densidad" to 0.0000015,
        "Plomo" to 0.0000015,
        "PVC" to 0.0000015,
        "Vidrio" to 0.0000015
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                navItemList = NavListMenu.navList,
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    when (index) {
                        0 -> navController.navigate(AppScreens.FirstScreen.route)
                        1 -> navController.navigate("diametro")
                        2 -> navController.navigate("caudal")
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.fondo_app),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            var D by remember { mutableStateOf("") }
            var L by remember { mutableStateOf("") }
            var rug by remember { mutableStateOf(0.0015) }
            var H by remember { mutableStateOf("") }
            var SK by remember { mutableStateOf("") }
            var res by remember { mutableStateOf(" ") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Titulo("Cálculo de Caudal")

                // Tarjeta para datos de entrada
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Datos de Entrada",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.text_blue)
                        )

                        InputWithUnit(
                            value = D,
                            onConvertedValueChange = { D = it },
                            labelText = "Diámetro",
                            icon = Icons.Default.Settings
                        )

                        InputWithUnit(
                            value = L,
                            onConvertedValueChange = { L = it },
                            labelText = "Longitud",
                            icon = Icons.Default.Settings
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            DropdownRugosidad(
                                materiales = materiales,
                                onValueSelected = { rug = it },
                                modifier = Modifier.width(300.dp)
                            )
                        }

                        Input_1(
                            value = H,
                            onValueChange = { H = it },
                            labelText = "Carga Total Disponible (m)",
                            icon = Icons.Default.Settings
                        )

                        Input_1(
                            value = SK,
                            onValueChange = { SK = it },
                            labelText = "Coef. de pérdidas locales",
                            icon = Icons.Default.Settings
                        )
                    }
                }

                // Tarjeta para resultados
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.text_blue).copy(alpha = 0.9f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Resultado del Cálculo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Caudal Calculado:",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                "$res L/s",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Botón de calcular
                Button(
                    onClick = {
                        val dVal = D.toDoubleOrNull()
                        val lVal = L.toDoubleOrNull()
                        val hVal = H.toDoubleOrNull()
                        val skVal = SK.toDoubleOrNull()

                        if (dVal != null && lVal != null && hVal != null && skVal != null) {
                            val area = Math.PI * dVal * dVal / 4
                            val q = calcularGasto(dVal, area, lVal, rug, hVal, skVal)
                            res = String.format("%.3f", q * 1000)
                        } else {
                            res = "0.000"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.text_blue),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Calcular Caudal", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// Función de cálculo (sin cambios)
fun calcularGasto(D: Double, A: Double, L: Double, Rug: Double, H: Double, SK: Double): Double {
    val g = 9.81
    val Nu = 0.000001
    var f = 0.018
    var ff = f
    var ep: Double

    do {
        val V = sqrt((2 * g * H) / ((f * L / D) + SK))
        val Re = (V * D) / Nu
        f = 0.25 / (log10(Rug / (3.7 * D) + 5.74 / Re.pow(0.9))).pow(2)
        ep = abs(ff - f)
        ff = f
    } while (ep >= 0.00001)

    val Vfinal = sqrt((2 * g * H) / ((f * L / D) + SK))
    return A * Vfinal
}
