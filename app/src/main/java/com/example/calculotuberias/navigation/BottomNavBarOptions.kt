package com.example.calculotuberias.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.calculotuberias.models.NavItem

@Composable
fun BottomNavBarOptions(
    navItemList: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar {
        navItemList.forEachIndexed { index, navItem ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = { Icon(imageVector = navItem.icono, contentDescription = "icono") },
                label = { Text(navItem.label) }
            )
        }
    }
}
