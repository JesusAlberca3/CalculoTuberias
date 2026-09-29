package com.example.calculotuberias.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calculotuberias.R
import com.example.calculotuberias.navigation.AppScreens
import com.example.calculotuberias.navigation.BottomNavBarOptions
import com.example.calculotuberias.navigation.NavList
import kotlin.math.*

@Composable
fun MenuScreen(navController: NavController) {
    var selectedIndex by remember { mutableIntStateOf(1) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        Image(
            painter = painterResource(R.drawable.fondo_app),
            contentDescription = "Fondo del menú",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Contenido sobre la imagen
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {},
            bottomBar = {
                BottomNavBarOptions(
                    navItemList = NavList.navItemList,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index -> selectedIndex = index }
                )
            }
        ) { innerPadding ->
            BodyContentMenu(
                modifier = Modifier.padding(innerPadding),
                selectedIndex = selectedIndex,
                navController = navController
            )
        }
    }
}

@Composable
fun BodyContentMenu(modifier: Modifier, selectedIndex: Int, navController: NavController) {
    when(selectedIndex) {
        0 -> navController.navigate(AppScreens.FirstScreen.route)
        1 -> PerdidasDW()
        2 -> PerdidasHW()
        3 -> PerdidasCM()
    }
}

@Composable
fun PerdidasDW() {
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

    var L by remember { mutableStateOf("") }
    var D by remember { mutableStateOf("") }
    var rugDouble by remember { mutableStateOf(0.0048) }
    var Q by remember { mutableStateOf("") }
    var res by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Titulo("Darcy-Weisbach")

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
                    value = L,
                    onConvertedValueChange = { L = it },
                    labelText = "Longitud",
                    Icons.Default.Settings
                )
                InputWithUnit(
                    value = D,
                    onConvertedValueChange = { D = it },
                    labelText = "Diámetro",
                    Icons.Default.Settings
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    DropdownRugosidad(
                        materiales = materiales,
                        onValueSelected = { rugDouble = it },
                        modifier = Modifier.width(300.dp)
                    )
                }

                Input_1(
                    value = Q,
                    onValueChange = { Q = it },
                    labelText = "Gasto (L/s)",
                    Icons.Default.Settings
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
                    "Resultados del Cálculo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (res.isNotEmpty()) "Pérdidas: $res m" else "Ingresa los datos y presiona calcular",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Botón de calcular
        Button(
            onClick = {
                val lDouble = L.toDoubleOrNull()
                val dDouble = D.toDoubleOrNull()
                val qDouble = Q.toDoubleOrNull()

                if (lDouble != null && dDouble != null && qDouble != null) {
                    res = String.format("%.3f", perdidasDW(lDouble, dDouble, rugDouble, qDouble))
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

@Composable
fun PerdidasHW() {
    val materialesCH = mapOf(
        "Nada" to 0,
        "Acero Esmaltado" to 148,
        "Acero Galvanizado" to 120,
        "Acero Nuevo" to 145,
        "Acero Remachado" to 110,
        "Asbesto Cemento" to 147,
        "Cobre" to 135,
        "Concreto (Cimbra Madera)" to 120,
        "Concreto (Cimbra Metálica)" to 140,
        "Concreto Simple" to 135,
        "Policloruro de Vinilo Clorado" to 150,
        "Hierro Dúctil" to 135,
        "Hierro Fundido" to 130,
        "Hierro Fundido Asfaltado" to 148,
        "Hierro Galvanizado" to 133,
        "Latón" to 135,
        "Madera Duelas" to 120,
        "Polietileno de Alta Densidad" to 150,
        "Plomo" to 135,
        "PVC" to 150,
        "Vidrio" to 140
    )

    var L by remember { mutableStateOf("") }
    var D by remember { mutableStateOf("") }
    var Q by remember { mutableStateOf("") }
    var CHDouble by remember { mutableStateOf(0.0) }
    var res by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Titulo("Hazen-Williams")

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
                    value = L,
                    onConvertedValueChange = { L = it },
                    labelText = "Longitud",
                    Icons.Default.Settings
                )

                InputWithUnit(
                    value = D,
                    onConvertedValueChange = { D = it },
                    labelText = "Diámetro",
                    Icons.Default.Settings
                )

                DropdownCH(
                    materialesCH = materialesCH,
                    onValueSelected = { valor -> CHDouble = valor.toDouble() },
                    modifier = Modifier.width(300.dp)
                )

                Input_1(
                    value = Q,
                    onValueChange = { Q = it },
                    labelText = "Gasto (L/s)",
                    Icons.Default.Settings
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
                    "Resultados del Cálculo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (res.isNotEmpty()) "Pérdidas: $res m" else "Ingresa los datos y presiona calcular",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Botón de calcular
        Button(
            onClick = {
                val lDouble = L.toDoubleOrNull()
                val dDouble = D.toDoubleOrNull()
                val qDouble = Q.toDoubleOrNull()

                if (lDouble != null && dDouble != null && qDouble != null && CHDouble > 0.0) {
                    res = String.format("%.3f", perdidasHW(lDouble, dDouble, CHDouble, qDouble))
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

@Composable
fun PerdidasCM() {
    val materiales = mapOf(
        "Acero esmaltado" to 0.010,
        "Acero galvanizado" to 0.016,
        "Acero nuevo" to 0.011,
        "Acero remachado" to 0.010,
        "Asbesto cemento" to 0.011,
        "Cobre" to 0.010,
        "Concreto (cimbra madera)" to 0.015,
        "Concreto (cimbra metálica)" to 0.013,
        "Concreto simple" to 0.013,
        "Policloruro de vinilo clorado" to 0.009,
        "Hierro dúctil" to 0.022,
        "Hierro fundido" to 0.012,
        "Hierro fundido asfaltado" to 0.012,
        "Hierro galvanizado" to 0.016,
        "Latón" to 0.011,
        "Madera duelas" to 0.012,
        "Polietileno de alta densidad" to 0.009,
        "Plomo" to 0.011,
        "PVC" to 0.009,
        "Vidrio" to 0.011
    )

    var L by remember { mutableStateOf("") }
    var D by remember { mutableStateOf("") }
    var Q by remember { mutableStateOf("") }
    var nDouble by remember { mutableStateOf(0.0) }
    var res by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Titulo("Chézy Manning")

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
                    value = L,
                    onConvertedValueChange = { L = it },
                    labelText = "Longitud",
                    Icons.Default.Settings
                )

                InputWithUnit(
                    value = D,
                    onConvertedValueChange = { D = it },
                    labelText = "Diámetro",
                    Icons.Default.Settings
                )

                DropdownRugosidad(
                    materiales = materiales,
                    onValueSelected = { nDouble = it },
                    modifier = Modifier.width(300.dp)
                )

                Input_1(
                    value = Q,
                    onValueChange = { Q = it },
                    labelText = "Gasto (L/s)",
                    Icons.Default.Settings
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
                    "Resultados del Cálculo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (res.isNotEmpty()) "Pérdidas: $res m" else "Ingresa los datos y presiona calcular",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Botón de calcular
        Button(
            onClick = {
                val lDouble = L.toDoubleOrNull()
                val dDouble = D.toDoubleOrNull()
                val qDouble = Q.toDoubleOrNull()

                if (lDouble != null && dDouble != null && qDouble != null && nDouble > 0.0) {
                    res = String.format("%.3f", perdidasCM(lDouble, dDouble, nDouble, qDouble))
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

// Mantenemos las mismas funciones de cálculo y componentes auxiliares
@Composable
fun Titulo(titulo: String) {
    val textColor = colorResource(id = R.color.text_blue)

    Text(
        text = titulo,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Center
    )
}

// Las funciones perdidasDW(), perdidasHW(), perdidasCM() y los componentes Input_1,
// InputWithUnit, DropdownRugosidad, DropdownCH se mantienen igual que en tu código original

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Input_1(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    icon: ImageVector
) {
    val textColor = colorResource(id = R.color.text_blue)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Fila para etiqueta e icono
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = textColor
            )
            Text(
                text = labelText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }

        // Campo de texto
        OutlinedTextField(
            value = value,
            onValueChange = {
                val formatted = it.replace(",", ".")
                if (formatted.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    onValueChange(formatted)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = colorResource(id = R.color.text_blue),
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}


fun perdidasDW(longitud: Double, diametro: Double, rugosidad: Double, gasto: Double): Double {
    val L = longitud
    val D = diametro  // YA VIENE EN METROS - NO DIVIDIR ENTRE 1000
    val rug = rugosidad / 1000.0  // rugosidad en mm -> m
    val Q = gasto / 1000.0  // L/s -> m³/s

    val nu = 1.004e-6  // Viscosidad cinemática del agua a 20°C (m²/s)
    val g = 9.81  // Gravedad (m/s²)

    val A = Math.PI * D * D / 4.0
    val V = Q / A
    val Re = V * D / nu

    // Fórmula de Swamee-Jain para fricción
    val f = if (Re > 0) {
        0.25 / Math.pow(Math.log10(rug/(3.7 * D) + 5.74/Math.pow(Re, 0.9)), 2.0)
    } else {
        0.02  // Valor por defecto
    }

    val hf1 = f * (L / D) * (V * V / (2.0 * g))
    return hf1
}
fun perdidasHW(longitud: Double, diametro: Double, coeficiente: Double, gasto: Double): Double {
    val L = longitud
    val D = diametro  // Ya en metros
    val CH = coeficiente
    val Q = gasto / 1000.0  // L/s -> m³/s

    val hf2 = (10.68 * L * Math.pow(Q, 1.85)) / (Math.pow(D, 4.85) * Math.pow(CH, 1.85))
    return hf2
}

fun perdidasCM(longitud: Double, diametro: Double, coeficiente: Double, gasto: Double): Double {
    val L = longitud
    val D = diametro  // Ya en metros
    val n = coeficiente
    val Q = gasto / 1000.0  // L/s -> m³/s

    val hf3 = 10.29 * L * n * n * Q * Q / Math.pow(D, 16.0 / 3.0)
    return hf3
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownRugosidad(
    materiales: Map<String, Double>,
    onValueSelected: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("PVC") } // Valor por defecto más común

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
                materiales.forEach { (material, valor) ->
                    DropdownMenuItem(
                        text = { Text(text = material) },
                        onClick = {
                            selectedText = material
                            expanded = false
                            onValueSelected(valor)
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCH(
    materialesCH: Map<String, Int>,
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(materialesCH.keys.first()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Coeficiente CH",
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
                materialesCH.forEach { (material, valor) ->
                    DropdownMenuItem(
                        text = {
                            Text("$material (CH = $valor)")
                        },
                        onClick = {
                            selectedText = material
                            onValueSelected(valor)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputWithUnit(
    value: String,
    onConvertedValueChange: (String) -> Unit,
    labelText: String,
    icon: ImageVector
) {
    val textColor = colorResource(id = R.color.text_blue)
    var inputValue by remember { mutableStateOf(value) }
    var expanded by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf("mm") }
    val unitOptions = listOf("mm", "cm", "m", "in")

    // Conversiones a METROS
    val conversionFactor = when (selectedUnit) {
        "mm" -> 0.001
        "cm" -> 0.01
        "m" -> 1.0
        "in" -> 0.0254
        else -> 1.0
    }

    // Se convierte el valor ingresado a metros y se notifica
    LaunchedEffect(inputValue, selectedUnit) {
        val numeric = inputValue.toDoubleOrNull()
        val converted = numeric?.times(conversionFactor)
        onConvertedValueChange(converted?.toString() ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Fila para etiqueta e icono
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = textColor
            )
            Text(
                text = labelText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }

        // Fila para input y selector de unidades
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Campo de texto
            OutlinedTextField(
                value = inputValue,
                onValueChange = {
                    val formatted = it.replace(",", ".")
                    if (formatted.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        inputValue = formatted
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = colorResource(id = R.color.text_blue),
                    unfocusedIndicatorColor = Color.Gray,
                    // Color del contenedor transparente
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            // Selector de unidades
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(56.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedUnit,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
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
                        onDismissRequest = { expanded = false }
                    ) {
                        unitOptions.forEach { unit ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = unit,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                onClick = {
                                    selectedUnit = unit
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}