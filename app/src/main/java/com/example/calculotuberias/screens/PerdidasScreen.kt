
package com.example.calculotuberias.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.calculotuberias.R
import com.example.calculotuberias.components.CardPerdidas
import com.example.calculotuberias.navigation.AppScreens
import com.example.calculotuberias.navigation.BottomNavBar
import com.example.calculotuberias.navigation.NavListMenu
import com.example.calculotuberias.viewModels.PerdidasViewModel
import kotlin.math.pow
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color

@SuppressLint("DefaultLocale")
@Composable
fun PerdidasScreen(navController: NavController, perdidasViewModel: PerdidasViewModel = viewModel()) {
    val accesorios by perdidasViewModel.accesoriosItems.observeAsState(emptyList())
    var selectedIndex by remember { mutableIntStateOf(2) }
    var Q by remember { mutableStateOf("") }
    var D by remember { mutableStateOf("") }
    var kTotal by remember { mutableStateOf(0.0) }
    var perdidas by remember { mutableStateOf(0.0) }

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
                        2 -> navController.navigate("perdidas")
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Titulo("Pérdidas por Accesorios")

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
                            labelText = "Caudal (m³/s)",
                            Icons.Default.Settings
                        )

                        Input_1(
                            value = D,
                            onValueChange = { D = it },
                            labelText = "Diámetro (m)",
                            Icons.Default.Settings
                        )
                    }
                }

                // Tarjeta para accesorios
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
                            .padding(16.dp)
                    ) {
                        Text(
                            "Selección de Accesorios",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.text_blue),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            "Selecciona los accesorios y su cantidad:",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(accesorios) { accesorio ->
                                CardPerdidas(
                                    aceesorio = accesorio,
                                    anadir = { perdidasViewModel.addCantidad(accesorio) },
                                    eliminar = { perdidasViewModel.removeCantidad(accesorio) }
                                )
                            }
                        }
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
                            "Resultados del Cálculo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Coeficiente Total (K)",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    "%.2f".format(kTotal),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    "Pérdida de Carga",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    "%.4f m".format(perdidas),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Botón de calcular
                Button(
                    onClick = {
                        val qDouble = Q.toDoubleOrNull()
                        val dDouble = D.toDoubleOrNull()
                        if (qDouble != null && dDouble != null && dDouble > 0) {
                            kTotal = perdidasViewModel.calcularTotal()
                            val area = Math.PI * dDouble * dDouble / 4.0
                            val velocidad = qDouble / area
                            perdidas = kTotal * (velocidad * velocidad) / (2.0 * 9.81)
                        } else {
                            // Opcional: mostrar mensaje de error si los datos no son válidos
                            kTotal = 0.0
                            perdidas = 0.0
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
                    Text("Calcular Pérdidas", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// También puedes mejorar el componente ResultadosPerdidas si lo quieres mantener separado:
@Composable
fun ResultadosPerdidas(kTotal: Double, perdidas: Double) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Coeficiente Total (K)",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        "%.2f".format(kTotal),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "Pérdida de Carga",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        "%.4f m".format(perdidas),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
