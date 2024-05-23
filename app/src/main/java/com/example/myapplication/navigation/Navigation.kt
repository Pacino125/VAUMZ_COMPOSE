package com.example.myapplication.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.screens.FishScreen
import com.example.myapplication.screens.LicenseScreen
import com.example.myapplication.screens.SelectAreaManuallyScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "license"
    ) {
        composable("license") {
            LicenseScreen()
        }
        composable("fish") {
            FishScreen(navController)
        }
        composable("select_area") {
            SelectAreaManuallyScreen()
        }
    }
}