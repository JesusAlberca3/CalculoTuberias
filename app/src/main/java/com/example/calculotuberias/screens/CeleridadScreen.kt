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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
fun CeleridadScreen(navController: NavController) {
    var selectedIndex by remember { mutableIntStateOf(2) }

    // Mapa de materiales con sus módulos de Young en GPa (luego convertimos a Pa)
    val materiales = mapOf(
        "Acero" to 207.0,
        "Hierro fundido" to 90.0,
        "Hierro dúctil" to 172.0,
        "Concreto" to 25.0,
        "Concreto reforzado" to 45.0,
        "Asbesto cemento" to 24.0,
        "PVC" to 3.3,
        "PEAD" to 0.8,
        "PRFV" to 15.0
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
                        2 -> navController.navigate("celeridad")
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

            var diametro by remember { mutableStateOf("") }
            var espesor by remember { mutableStateOf("") }
            var moduloYoung by remember { mutableStateOf(0.0) }
            var materialSeleccionado by remember { mutableStateOf("PVC") }
            var resultado by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Titulo("Cálculo de Celeridad")

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

                        // Usar InputWithUnit para diámetro y espesor
                        InputWithUnit(
                            value = diametro,
                            onConvertedValueChange = { diametro = it },
                            labelText = "Diámetro interior",
                            Icons.Default.Settings
                        )

                        InputWithUnit(
                            value = espesor,
                            onConvertedValueChange = { espesor = it },
                            labelText = "Espesor de la tubería",
                            Icons.Default.Settings
                        )

                        // Dropdown para selección de material con el mismo estilo
                        DropdownMaterialCeleridad(
                            materiales = materiales,
                            onValueSelected = { material, modulo ->
                                materialSeleccionado = material
                                moduloYoung = modulo * 1e9 // Convertir GPa a Pa
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Mostrar el módulo de Young seleccionado
                        if (moduloYoung > 0) {
                            Text(
                                "Módulo de Young seleccionado: ${"%.1f".format(moduloYoung / 1e9)} GPa",
                                fontSize = 14.sp,
                                color = colorResource(id = R.color.text_blue),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Text(
                            "Constantes utilizadas:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(id = R.color.text_blue),
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Text(
                            "• Densidad del agua (ρ) = 998 kg/m³\n" +
                                    "• Módulo de compresibilidad (K) = 2.19×10⁹ Pa",
                            fontSize = 12.sp,
                            color = Color.Gray
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

                        if (resultado.isNotEmpty()) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Celeridad calculada:",
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        resultado,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    "Material: $materialSeleccionado",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        } else {
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
                        val dDouble = diametro.toDoubleOrNull()
                        val eDouble = espesor.toDoubleOrNull()

                        if (dDouble != null && eDouble != null && moduloYoung > 0) {
                            val celeridad = calcularCeleridad(dDouble, eDouble, moduloYoung)
                            resultado = String.format("%.2f m/s", celeridad)
                        } else {
                            resultado = "Error: Complete todos los campos"
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
                    Text("Calcular Celeridad", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// Dropdown para materiales con el mismo estilo que DropdownRugosidad
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMaterialCeleridad(
    materiales: Map<String, Double>,
    onValueSelected: (String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("PVC") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Material de la tubería",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(id = R.color.text_blue),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = colorResource(id = R.color.text_blue),
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                materiales.forEach { (material, modulo) ->
                    DropdownMenuItem(
                        text = {
                            Text("$material (${modulo} GPa)")
                        },
                        onClick = {
                            selectedText = material
                            expanded = false
                            onValueSelected(material, modulo)
                        }
                    )
                }
            }
        }
    }
}

// Función para calcular la celeridad - ACTUALIZADA para usar metros directamente
fun calcularCeleridad(diametroMetros: Double, espesorMetros: Double, moduloYoung: Double): Double {
    val rho = 998.0 // densidad del agua a 20°C, en kg/m3
    val K = 2.19e9  // módulo de compresibilidad del agua a 20°C, en Pascales

    // Ya vienen en metros desde InputWithUnit, no necesitamos conversión
    val D = diametroMetros
    val e = espesorMetros
    val Y = moduloYoung

    // Calcular celeridad usando la fórmula
    val c = 1.0 / Math.sqrt(rho * ((1.0 / K) + (D / (Y * e))))
    return c
}