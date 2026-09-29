package com.example.calculotuberias.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import com.example.calculotuberias.models.NavItem
import kotlin.collections.listOf

object NavListMenu {
    val navList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Menu", Icons.Default.Menu)
    )
}