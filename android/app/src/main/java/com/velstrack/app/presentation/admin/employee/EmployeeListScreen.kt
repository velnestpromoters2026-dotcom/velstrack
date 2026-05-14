package com.velstrack.app.presentation.admin.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.velstrack.app.core.theme.DeepSpaceBlack
import com.velstrack.app.core.theme.ElectricIndigo
import com.velstrack.app.core.theme.EmeraldSuccess
import com.velstrack.app.core.theme.NeonCyan
import com.velstrack.app.core.util.UiState
import com.velstrack.app.presentation.admin.AdminViewModel
import com.velstrack.app.presentation.components.EmptyStateCard
import com.velstrack.app.presentation.components.ErrorStateCard
import com.velstrack.app.presentation.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    onBack: () -> Unit,
    onAddEmployee: () -> Unit,
    onEmployeeClick: (String) -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.employeesState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEmployees()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Team Directory", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSpaceBlack,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEmployee,
                containerColor = ElectricIndigo,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Employee")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepSpaceBlack)
                .padding(paddingValues)
        ) {
            when (state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NeonCyan
                    )
                }
                is UiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyStateCard(
                            title = "No Team Members",
                            message = "Your employee directory is currently empty. Add team members to start tracking performance.",
                            icon = Icons.Default.Person
                        )
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorStateCard(
                            message = (state as UiState.Error).message,
                            onRetry = { viewModel.loadEmployees() }
                        )
                    }
                }
                is UiState.Success -> {
                    val employees = (state as UiState.Success).data
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(employees) { emp ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(ElectricIndigo.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ElectricIndigo)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = emp.name, style = MaterialTheme.typography.titleMedium)
                                        Text(text = emp.role, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (emp.isOnline == true) {
                                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(EmeraldSuccess))
                                    }
                                }
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}
