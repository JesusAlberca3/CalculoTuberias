package com.example.calculotuberias.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.calculotuberias.R
import com.example.calculotuberias.navigation.AppScreens
import com.example.calculotuberias.navigation.BottomNavBar
import com.example.calculotuberias.navigation.NavListMenu
import kotlin.math.*
import kotlin.system.measureTimeMillis

@SuppressLint("DefaultLocale")
@Composable
fun DiametroScreen(navController: NavController) {
    var selectedIndex by remember { mutableIntStateOf(1) }

    val materiales = mapOf(
        "Nada" to 0.0,
        "Acero Esmaltado" to 0.0048,
        "Acero Galvanizado" to 0.15,
        "Acero Nuevo" to 0.045,
        "Acero Remachado" to 0.9,
        "Asbesto Cemento" to 0.03,
        "Cobre" to 0.0015,
        "Concreto (Cimbra Madera)" to 0.6,
        "Concreto (Cimbra Metálica)" to 0.18,
        "Concreto Simple" to 0.36,
        "Policloruro de Vinilo Clorado" to 0.0015,
        "Hierro Dúctil" to 0.25,
        "Hierro Fundido" to 0.15,
        "Hierro Fundido Asfaltado" to 0.12,
        "Hierro Galvanizado" to 0.15,
        "Latón" to 0.0015,
        "Madera Duelas" to 0.18,
        "Polietileno de Alta Densidad" to 0.001524,
        "Plomo" to 0.0015,
        "PVC" to 0.0015,
        "Vidrio" to 0.0015
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

            var Q by remember { mutableStateOf("") }
            var L by remember { mutableStateOf("") }
            var rug by remember { mutableStateOf(0.0015) }
            var H by remember { mutableStateOf("") }
            var SK by remember { mutableStateOf("") }

            var diametroM by remember { mutableStateOf("") }
            var diametroMM by remember { mutableStateOf("") }
            var diametroPulgadas by remember { mutableStateOf("") }
            var iteraciones by remember { mutableStateOf("") }
            var tiempo by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Titulo("Diámetro por Gravedad")

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

                        Input_1(
                            value = Q,
                            onValueChange = { Q = it },
                            labelText = "Gasto (L/s)",
                            Icons.Default.Settings
                        )

                        InputWithUnit(
                            value = L,
                            onConvertedValueChange = { L = it },
                            labelText = "Longitud",
                            Icons.Default.Settings
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
                            labelText = "Carga total H (m)",
                            Icons.Default.Settings
                        )

                        Input_1(
                            value = SK,
                            onValueChange = { SK = it },
                            labelText = "Coef. pérdidas",
                            Icons.Default.Settings
                        )
                    }
                }

                // Tarjeta para resultados (solo se muestra cuando hay datos)
                if (diametroM.isNotEmpty()) {
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
                                "Resultados del Cálculo",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Primera fila de resultados
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Diámetro (m)",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        diametroM,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        "Diámetro (mm)",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        diametroMM,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Segunda fila de resultados
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Diámetro (pulg)",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        diametroPulgadas,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        "Iteraciones",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        iteraciones,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Tercera fila - tiempo
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Tiempo de cálculo:",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    tiempo,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else {
                    // Tarjeta de resultados vacía (para mantener el espacio)
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
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Resultados del Cálculo",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Ingresa los datos y presiona calcular",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // Botón de calcular
                Button(
                    onClick = {
                        val qDouble = Q.toDoubleOrNull()?.div(1000)
                        val lDouble = L.toDoubleOrNull()
                        val hDouble = H.toDoubleOrNull()
                        val skDouble = SK.toDoubleOrNull()

                        if (qDouble != null && lDouble != null && hDouble != null && skDouble != null) {
                            val tiempoMs = measureTimeMillis {
                                val (D, iter) = calcularDiametro(qDouble, lDouble, rug, hDouble, skDouble)
                                diametroM = String.format("%.4f m", D)
                                diametroMM = String.format("%.2f mm", D * 1000)
                                diametroPulgadas = String.format("%.2f in", D / 0.0254)
                                iteraciones = "$iter iteraciones"
                            }
                            tiempo = String.format("%.3f s", tiempoMs / 1000.0)
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
                    Text("Calcular Diámetro", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


//@Composable
//
//fun Resultados(
//    diametroM: String,
//    diametroMM: String,
//    diametroPulgadas: String,
//    iteraciones: String,
//    tiempo: String
//)
//{
//    val textColor = colorResource(id = R.color.text_blue)
//    Column(
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth(),
//        horizontalAlignment = Alignment.Start
//    ) {
//        Text("Resultados:", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
//        Spacer(modifier = Modifier.height(8.dp))
//        Text("Diámetro (m): $diametroM", color = textColor, fontSize = 18.sp)
//        Text("Diámetro (mm): $diametroMM", color = textColor, fontSize = 18.sp)
//        Text("Diámetro (pulgadas): $diametroPulgadas", color = textColor, fontSize = 18.sp)
//        Text("Iteraciones: $iteraciones", color = textColor, fontSize = 18.sp)
//        Text("Tiempo de cálculo: $tiempo ", color = textColor, fontSize = 18.sp)
//    }
//}

fun calcularDiametro(Q: Double, L: Double, rug: Double, H: Double, SK: Double): Pair<Double, Int> {
    val nu = 1e-6    // viscosidad cinemática del agua a 20°C [m²/s]
    val g = 9.81     // gravedad [m/s²]

    var D = 0.5     // diámetro supuesto inicial [m]  <-- MATLAB usa 0.5, no 0.01
    var dAnterior: Double
    var ep = 1.0
    var i = 0

    val rugosidadM = rug / 1000.0  // ✅ convertir de mm a m (igual que en MATLAB)

    while (ep != 0.0 && i < 1000) {
        val A = Math.PI * D * D / 4.0
        val V = Q / A
        val Re = (V * D) / nu
        val f = 0.25 / (log10(rugosidadM / (3.7 * D) + 5.74 / Re.pow(0.9))).pow(2)
        val Vnuevo = sqrt((2 * g * H) / (1 + (f * (L / D)) + SK))
        dAnterior = D
        D = sqrt((4 * Q) / (Math.PI * Vnuevo))
        ep = dAnterior - D
        i++
    }

    return Pair(D, i)
}


