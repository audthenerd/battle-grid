package com.example.battlegrid.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Navigation destinations
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Controls : Screen("controls", "Controls", Icons.Filled.Settings)
    object User : Screen("user", "User", Icons.Filled.AccountCircle)
} 