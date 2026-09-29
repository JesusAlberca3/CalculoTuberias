package com.example.calculotuberias.navigation

sealed class AppScreens (val route: String){
    object FirstScreen: AppScreens("Bienvenida")
    object MenuScreen: AppScreens("Menu")
    object MenuOptionsScreen: AppScreens("MenuChido")
    object MenuOptionsLostScreen : AppScreens("MenuPerdidas")
    object CaudalScreen : AppScreens("Caudal")
    object DiametroScreen : AppScreens("Diametro")
    object PerdidasScreen : AppScreens("Perdidas")
    object CeleridadScreen : AppScreens("Celeridad")
    object CanalesScreen : AppScreens("Canales")
}