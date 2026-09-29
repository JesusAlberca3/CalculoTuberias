package com.example.calculotuberias.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculotuberias.ui.screens.DocumentacionScreen
import com.example.calculotuberias.ui.screens.CanalesScreen
import com.example.calculotuberias.ui.screens.CaudalScreen
import com.example.calculotuberias.ui.screens.CeleridadScreen
import com.example.calculotuberias.ui.screens.DiametroScreen
import com.example.calculotuberias.ui.screens.InicioScreen
import com.example.calculotuberias.ui.screens.MenuConductosScreen
import com.example.calculotuberias.ui.screens.MenuPerdidasScreen
import com.example.calculotuberias.ui.screens.PerdidasFriccionScreen
import com.example.calculotuberias.ui.screens.PerdidasLocalesScreen

@Composable
fun GrafoNavegacion() {
    val navController = rememberNavController()
    val volver: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = Inicio) {

        composable<Inicio> {
            InicioScreen(
                irAConductos = { navController.navigate(MenuConductos) },
                irACanales = { navController.navigate(CanalesRuta) },
                irAAcercaDe = { navController.navigate(AcercaDe) },
            )
        }

        composable<MenuConductos> {
            MenuConductosScreen(
                onVolver = volver,
                irACaudal = { navController.navigate(Caudal) },
                irADiametro = { navController.navigate(Diametro) },
                irAPerdidas = { navController.navigate(MenuPerdidas) },
                irACeleridad = { navController.navigate(CeleridadRuta) },
            )
        }

        composable<MenuPerdidas> {
            MenuPerdidasScreen(
                onVolver = volver,
                irAFriccion = { navController.navigate(PerdidasPorFriccion) },
                irALocales = { navController.navigate(PerdidasLocalesRuta) },
            )
        }

        composable<Caudal> { CaudalScreen(onVolver = volver) }
        composable<Diametro> { DiametroScreen(onVolver = volver) }
        composable<PerdidasPorFriccion> { PerdidasFriccionScreen(onVolver = volver) }
        composable<PerdidasLocalesRuta> { PerdidasLocalesScreen(onVolver = volver) }
        composable<CeleridadRuta> { CeleridadScreen(onVolver = volver) }
        composable<CanalesRuta> { CanalesScreen(onVolver = volver) }
        composable<AcercaDe> { DocumentacionScreen(onVolver = volver) }
    }
}
