package com.velstrack.app.presentation.admin.tables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.velstrack.app.core.theme.*
import com.velstrack.app.core.util.UiState
import com.velstrack.app.presentation.admin.AdminViewModel
import com.velstrack.app.presentation.components.ErrorStateCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveStaffScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.employeesState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEmployees()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Staff", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PureWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AbsoluteBlack,
                    titleContentColor = PureWhite
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AbsoluteBlack)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceGray)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Name", color = PureWhite, modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                    Text("Role", color = PureWhite, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Status", color = PureWhite, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                }

                when (state) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally), color = PureWhite)
                    is UiState.Error -> ErrorStateCard(message = (state as UiState.Error).message, onRetry = { viewModel.loadEmployees() })
                    is UiState.Success -> {
                        val employees = (state as UiState.Success).data
                        LazyColumn {
                            items(employees) { emp ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Text(emp.name, color = MetallicSilver, modifier = Modifier.weight(1.5f))
                                    Text(emp.role, color = MetallicSilver, modifier = Modifier.weight(1f))
                                    
                                    val statusColor = if (emp.isOnline == true) EmeraldSuccess else RoseDanger
                                    val statusText = if (emp.isOnline == true) "Online" else "Offline"
                                    
                                    Text(statusText, color = statusColor, modifier = Modifier.weight(1f))
                                }
                                Divider(color = SurfaceGray)
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}
