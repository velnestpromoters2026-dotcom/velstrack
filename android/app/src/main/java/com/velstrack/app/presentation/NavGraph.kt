package com.velstrack.app.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.velstrack.app.presentation.auth.LoginScreen
import com.velstrack.app.presentation.splash.SplashScreen

@Composable
fun RootNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                // Future: Check SessionManager for existing token
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        
        composable("login") {
            LoginScreen(onLoginSuccess = { role ->
                // Future: Navigate to Dashboard 
            })
        }
    }
}
