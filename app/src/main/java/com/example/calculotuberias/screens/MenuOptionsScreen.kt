package com.example.calculotuberias.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calculotuberias.R
import com.example.calculotuberias.navigation.AppScreens
import com.example.calculotuberias.navigation.BottomNavBar
import com.example.calculotuberias.navigation.NavList
import com.example.calculotuberias.navigation.NavListMenu

@Composable
fun MenuOptionsScreen(navController: NavController){
    var selectedIndex by remember {mutableIntStateOf(1)}
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                navItemList = NavListMenu.navList,
                selectedIndex = selectedIndex,
                onItemSelected = {index ->
//                    if(selectedIndex==0){
//                        navController.navigate(AppScreens.FirstScreen.route)
//                    }
                    selectedIndex = index

                },
            )
        }
    ) { innerPadding ->
        BodyContentMenuScreen(modifier = Modifier.padding(innerPadding), navController, selectedIndex)
    }
}

@Composable
fun BodyContentMenuScreen(modifier: Modifier, navController: NavController, selectedIndex:Int){
    when(selectedIndex){
        0 -> navController.navigate(AppScreens.FirstScreen.route)
        1 -> BodyContentMenuOptions(navController)
    }
}

@Composable
fun BodyContentMenuOptions(navController: NavController){
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        Image(
            painter = painterResource(R.drawable.fondo_app),
            contentDescription = "Imagen de fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Agregar scroll
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botones con altura reducida y más espaciado
            BotonNavegacionMenuOption("CAUDAL", navController, AppScreens.CaudalScreen.route)
            Spacer(modifier = Modifier.height(16.dp))

            BotonNavegacionMenuOption("DIÁMETRO", navController, AppScreens.DiametroScreen.route)
            Spacer(modifier = Modifier.height(16.dp))

            BotonNavegacionMenuOption("PÉRDIDAS", navController, AppScreens.MenuOptionsLostScreen.route)
            Spacer(modifier = Modifier.height(16.dp))

            // Nuevo botón CELERIDAD
            BotonNavegacionMenuOption("CELERIDAD", navController, AppScreens.CeleridadScreen.route) // Cambia la ruta según necesites

            // Espacio extra al final para mejor scroll
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BotonNavegacionMenuOption(textoBoton: String, navController: NavController, ruta: String){
    val azul = colorResource(id = R.color.medium_blue)
    val textColor = colorResource(id = R.color.text_blue)
    Button(onClick = { navController.navigate(ruta) },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth(0.85f) // Reducir ancho para mejor aspecto
            .height(120.dp) // Reducir altura
            .padding(horizontal = 32.dp, vertical = 8.dp), // Menos padding vertical
        border = BorderStroke(
            width = 3.dp,
            color = Color(azul.toArgb())
        )
    ) {
        Text(
            textoBoton,
            fontSize = 22.sp, // Reducir tamaño de fuente ligeramente
            color = textColor,
            fontWeight = FontWeight.Bold // Agregar negrita para mejor legibilidad
        )
    }
}