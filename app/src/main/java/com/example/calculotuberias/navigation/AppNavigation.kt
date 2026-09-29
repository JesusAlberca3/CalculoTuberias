package com.example.calculotuberias.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculotuberias.screens.CaudalScreen
import com.example.calculotuberias.screens.DiametroScreen
import com.example.calculotuberias.screens.FirstScreen
import com.example.calculotuberias.screens.MenuOptionsLostScreen
import com.example.calculotuberias.screens.MenuOptionsScreen
import com.example.calculotuberias.screens.MenuScreen
import com.example.calculotuberias.screens.PerdidasScreen
import com.example.calculotuberias.screens.CeleridadScreen
import com.example.calculotuberias.screens.CanalesScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination= AppScreens.FirstScreen.route){
        composable(route = AppScreens.FirstScreen.route){
            FirstScreen(navController)
        }
        composable (route = AppScreens.MenuScreen.route){
            MenuScreen(navController)
        }
        composable (route = AppScreens.MenuOptionsScreen.route){
            MenuOptionsScreen(navController)
        }
        composable(route = AppScreens.MenuOptionsLostScreen.route) {
            MenuOptionsLostScreen(navController)
        }
        composable(route = AppScreens.CaudalScreen.route) {
            CaudalScreen(navController)
        }
        composable(route = AppScreens.DiametroScreen.route) {
            DiametroScreen(navController)
        }
        composable(route = AppScreens.PerdidasScreen.route){
            PerdidasScreen(navController)
        }
        composable(route = AppScreens.CeleridadScreen.route){
            CeleridadScreen(navController)
        }
        composable(route = AppScreens.CanalesScreen.route) {
            CanalesScreen(navController)
        }

    }

}