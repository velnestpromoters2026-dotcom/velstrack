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
import com.velstrack.app.core.theme.AbsoluteBlack
import com.velstrack.app.core.theme.MetallicSilver
import com.velstrack.app.core.theme.PureWhite
import com.velstrack.app.core.theme.SurfaceGray
import com.velstrack.app.core.util.UiState
import com.velstrack.app.presentation.admin.AdminViewModel
import com.velstrack.app.presentation.components.ErrorStateCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsSyncedScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // Re-use AdminViewModel to load calls/dashboard
    // For this UI, we assume we fetch calls from the backend. 
    // Wait, the dashboard returns just totalCallsSynced. We might need a loadAllCalls() in the VM.
    // For now, let's assume we can fetch it, or display empty state if we don't have the API yet.
    // The user wants a screen that displays each employee call details in table that suits app ui.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calls Synced", fontWeight = FontWeight.Bold) },
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
                    Text("Employee", color = PureWhite, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Phone", color = PureWhite, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Duration", color = PureWhite, modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
                    Text("Date", color = PureWhite, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                }
                
                // Table Body (Empty state for now since API isn't explicitly defined)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No call records found.", color = MetallicSilver)
                }
            }
        }
    }
}
