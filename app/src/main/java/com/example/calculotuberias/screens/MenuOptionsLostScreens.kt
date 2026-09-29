package com.example.calculotuberias.screens

import android.R.attr.icon
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calculotuberias.R
import com.example.calculotuberias.navigation.AppScreens
import com.example.calculotuberias.navigation.BottomNavBar
import com.example.calculotuberias.navigation.NavListMenu


@Composable
fun MenuOptionsLostScreen(navController: NavController) {
    val textColor = colorResource(id = R.color.text_blue)
    val azul = colorResource(id = R.color.medium_blue)
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
        ) {
            // Título
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .border(BorderStroke(3.dp, Color(azul.toArgb())))
                        .background(Color.White)
                ) {
                    Text(
                        text = "Determinación de perdidas por:",
                        modifier = Modifier.padding(16.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Serif,
                            color = textColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones
            BotonNavegacionMenuLostsOption("FRICCIÓN", navController, AppScreens.MenuScreen.route, R.drawable.perdidas_friccion)
            BotonNavegacionMenuLostsOption("LOCALES", navController, AppScreens.PerdidasScreen.route, R.drawable.perdidas_locales)
        }
    }
}




@Composable
fun BotonNavegacionMenuLostsOption(
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
