package com.example.calculotuberias.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import com.example.calculotuberias.models.NavItem
import kotlin.collections.listOf

object NavList {
    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("DW", Icons.Default.Build),
        NavItem("HW", Icons.Default.Build),
        NavItem("CM", Icons.Default.Build),
    )
}