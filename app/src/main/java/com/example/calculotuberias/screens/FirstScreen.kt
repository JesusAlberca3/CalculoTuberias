package com.example.calculotuberias.screens

import android.content.res.Resources
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calculotuberias.R
import com.example.calculotuberias.navigation.AppScreens
import com.example.calculotuberias.ui.theme.IrishGroverFont
import com.example.calculotuberias.ui.theme.PacificoRegular
import com.example.calculotuberias.ui.theme.PoppinsFontFamily
import com.example.calculotuberias.ui.theme.Yatra
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.text.style.TextAlign


//@Composable
//fun FirstScreen(navController: NavController){
//    Scaffold (
//        topBar = {TopContentFirstScreen()},
//    ){ innerPadding ->
//        BodyContentFirstScreen(modifier = Modifier.padding(innerPadding), navController)
//    }
//}

@Composable
fun FirstScreen(navController: NavController) {
   val morado = colorResource(id = R.color.medium_blue)
    val textColor = colorResource(id = R.color.text_blue)
    var showDialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        Image(
            painter = painterResource(R.drawable.fondo_app),
            contentDescription = "Imagen de fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            androidx.compose.material3.IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Acerca de",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }
        }


        // Título
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .border(BorderStroke(3.dp, Color(morado.toArgb())))
                        .background(Color.White)
                        .fillMaxWidth() // Añadir esto
                ) {
                    Text(
                        text = "Calculadora Hidráulica",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), // Añadir esto
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Serif,
                            color = textColor,
                        ),
                        textAlign = TextAlign.Center // Añadir esta línea
                    )
                }
            }

            if (showDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(text = "Acerca de")
                    },
                    text = {
                        Text("Esta aplicación fue desarrollada como parte de un proyecto académico de la UNAM FES Aragón. Su propósito es facilitar el cálculo hidráulico en conductos y canales.")
                    },
                    confirmButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cerrar")
                        }
                    }
                )
            }

//            Spacer(modifier = Modifier.height(24.dp))

            // Botones
            BotonNavegacionInicio("Conductos a Presión", navController, AppScreens.MenuOptionsScreen.route, R.drawable.conductos_a_presion)
            BotonNavegacionInicio("Canales", navController, AppScreens.CanalesScreen.route, R.drawable.conductos_a_presion)

            Spacer(modifier = Modifier.height(70.dp))

            Image(
                painter = painterResource(id = R.drawable.cintillo),
                contentDescription = "Cintillo UNAM FES Aragón",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            )
        }
    }
}

@Composable
fun BotonNavegacionInicio(
    textoBoton: String,
    navController: NavController,
    ruta: String,
    iconRes: Int // Cambiamos de ImageVector a Int (resource ID)
) {
    val azul = colorResource(id = R.color.light_blue_2)
    val textColor = colorResource(id = R.color.text_blue)

    Button(
        onClick = { navController.navigate(ruta) },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Blue
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(20.dp),
        border = BorderStroke(
            width = 3.dp,
            color = Color(azul.toArgb())
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Cambiamos Icon por Image para usar recursos drawable
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                textoBoton,
                fontSize = 25.sp,
                color = textColor
            )
        }
    }
}


