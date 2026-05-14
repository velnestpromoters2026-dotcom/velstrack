package com.velstrack.app.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.velstrack.app.presentation.auth.LoginScreen
import com.velstrack.app.presentation.splash.SplashScreen
import com.velstrack.app.presentation.employee.EmployeeDashboardScreen
import com.velstrack.app.presentation.admin.AdminDashboardScreen
import com.velstrack.app.presentation.admin.employee.EmployeeListScreen
import com.velstrack.app.presentation.admin.employee.AddEmployeeScreen
import com.velstrack.app.presentation.admin.meta.MetaDashboardScreen

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
                if (role == "EMPLOYEE") {
                    navController.navigate("employee_dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("admin_dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            })
        }
        
        composable("employee_dashboard") {
            EmployeeDashboardScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onManageEmployees = {
                    navController.navigate("employee_list")
                },
                onMetaAnalytics = {
                    navController.navigate("meta_dashboard")
                }
            )
        }

        composable("meta_dashboard") {
            MetaDashboardScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("employee_list") {
            EmployeeListScreen(
                onBack = { navController.popBackStack() },
                onAddEmployee = { navController.navigate("add_employee") },
                onEmployeeClick = { id -> /* TODO: Detail screen */ }
            )
        }

        composable("add_employee") {
            AddEmployeeScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
