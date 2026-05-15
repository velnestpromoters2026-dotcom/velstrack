package com.velstrack.app.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.velstrack.app.presentation.auth.LoginScreen
import com.velstrack.app.presentation.splash.SplashScreen
import com.velstrack.app.presentation.employee.EmployeeDashboardScreen
import com.velstrack.app.presentation.employee.dialer.DialerScreen
import com.velstrack.app.presentation.employee.dialer.ActiveCallScreen
import com.velstrack.app.presentation.admin.AdminMainScreen
import com.velstrack.app.presentation.admin.employee.AddEmployeeScreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.velstrack.app.core.datastore.SessionManager

@Composable
fun RootNavGraph() {
    val navController = rememberNavController()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        com.velstrack.app.MainActivity.callActionFlow.collect { number ->
            navController.navigate("active_call/$number")
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context.applicationContext) }
            val role by sessionManager.getUserRole().collectAsState(initial = null)
            val token by sessionManager.getJwtToken().collectAsState(initial = null)

            SplashScreen(onSplashFinished = {
                if (!token.isNullOrEmpty() && !role.isNullOrEmpty()) {
                    if (role == "EMPLOYEE") {
                        navController.navigate("employee_dashboard") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("admin_dashboard") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
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
        
        composable("employee_dashboard") { backStackEntry ->
            val viewModel: com.velstrack.app.presentation.employee.EmployeeDashboardViewModel = androidx.hilt.navigation.compose.hiltViewModel()

            EmployeeDashboardScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToDialer = {
                    navController.navigate("dialer")
                }
            )
        }

        composable("dialer") {
            DialerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToActiveCall = { number ->
                    navController.navigate("active_call/$number")
                }
            )
        }

        composable("active_call/{number}") { backStackEntry ->
            val number = backStackEntry.arguments?.getString("number") ?: "Unknown"
            ActiveCallScreen(
                phoneNumber = number,
                onCallEnded = { durationSeconds ->
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val prefs = context.getSharedPreferences("velstrack_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit()
                        .putInt("completed_call_duration", durationSeconds)
                        .putString("completed_call_number", number)
                        .apply()
                    
                    navController.popBackStack("employee_dashboard", false)
                }
            )
        }

        composable("admin_dashboard") {
            AdminMainScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToAddEmployee = {
                    navController.navigate("add_employee")
                }
            )
        }

        composable("add_employee") {
            AddEmployeeScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
