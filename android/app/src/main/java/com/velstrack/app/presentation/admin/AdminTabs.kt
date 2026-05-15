package com.velstrack.app.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.velstrack.app.core.theme.*
import com.velstrack.app.core.util.UiState
import com.velstrack.app.presentation.components.*

@Composable
fun AdminTeamTab(viewModel: AdminViewModel, onNavigateToAddEmployee: () -> Unit) {
    val state by viewModel.employeesState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEmployees()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = NeonCyan)
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
                    contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text("Team Directory", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
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

        FloatingActionButton(
            onClick = onNavigateToAddEmployee,
            containerColor = ElectricIndigo,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Employee")
        }
    }
}

@Composable
fun AdminTargetsTab(viewModel: AdminViewModel) {
    val state by viewModel.targetsState.collectAsState()
    val employeesState by viewModel.employeesState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadTargets()
        viewModel.loadEmployees()
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (state) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = NeonCyan)
            is UiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyStateCard(
                        title = "No Active Targets",
                        message = "Create targets to assign goals to your team members.",
                        icon = Icons.Default.CheckCircle
                    )
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ErrorStateCard(
                        message = (state as UiState.Error).message,
                        onRetry = { viewModel.loadTargets() }
                    )
                }
            }
            is UiState.Success -> {
                val targets = (state as UiState.Success).data
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Text("Target Management", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(targets) { target ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Type: ${target.targetType}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                    Text(text = target.status, color = if(target.status == "ACTIVE") EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Goal: ${target.targetValue} Calls", color = MaterialTheme.colorScheme.onSurface)
                                Text("Achieved: ${target.achievedValue}", color = NeonCyan)
                                
                                val progress = if (target.targetValue > 0) target.achievedValue.toFloat() / target.targetValue else 0f
                                LinearProgressIndicator(
                                    progress = progress,
                                    modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 8.dp),
                                    color = ElectricIndigo,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            else -> Unit
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NeonCyan,
            contentColor = DeepSpaceBlack,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Target")
        }

        if (showAddDialog) {
            val employees = (employeesState as? UiState.Success)?.data ?: emptyList()
            TargetAssignmentDialog(
                employees = employees,
                onDismiss = { showAddDialog = false },
                onSubmit = { req ->
                    viewModel.createTarget(req)
                    showAddDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCampaignsTab(viewModel: AdminViewModel) {
    val metaStatusState by viewModel.metaStatusState.collectAsState()
    val campaignState by viewModel.metaState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMetaStatus()
        viewModel.loadMetaCampaigns()
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (metaStatusState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = NeonCyan)
            is UiState.Error -> {
                ErrorStateCard(message = (metaStatusState as UiState.Error).message, onRetry = { viewModel.loadMetaStatus() })
            }
            is UiState.Success -> {
                val status = (metaStatusState as UiState.Success).data
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Text("Campaign Center", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        GlassCard {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = NeonCyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Meta Connection State", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = status.message, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Badge(containerColor = if (status.state == "ACTIVE_DELIVERY") EmeraldSuccess else RoseDanger) {
                                    Text(status.state, modifier = Modifier.padding(4.dp))
                                }
                            }
                        }
                    }
                    
                    if (status.state == "ACTIVE_DELIVERY" || status.state == "CAMPAIGNS_INACTIVE") {
                        if (campaignState is UiState.Success) {
                            val campaigns = (campaignState as UiState.Success).data
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Active Campaigns", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                            }
                            items(campaigns) { campaign ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = campaign.campaignName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                            Text(text = campaign.status, style = MaterialTheme.typography.labelMedium, color = if (campaign.status == "ACTIVE") NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column {
                                                Text(text = "Spend", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(text = "$${campaign.spend}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                            Column {
                                                Text(text = "CPC", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(text = "$${campaign.cpc}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                            Column {
                                                Text(text = "CTR", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(text = "${campaign.ctr}%", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
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

@Composable
fun AdminAnalyticsTab(viewModel: AdminViewModel) {
    val state by viewModel.analyticsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAnalytics()
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (state) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = NeonCyan)
            is UiState.Error -> ErrorStateCard(message = (state as UiState.Error).message, onRetry = { viewModel.loadAnalytics() })
            is UiState.Success -> {
                val data = (state as UiState.Success).data
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Text("Enterprise Analytics", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    item {
                        GlassCard {
                            Column {
                                Text("Weekly Call Volume", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(8.dp))
                                // A simple visualization of daily calls using progress bars
                                data.dailyCalls.forEach { day ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Text(day.date, modifier = Modifier.width(40.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        LinearProgressIndicator(
                                            progress = day.calls / 100f,
                                            modifier = Modifier.weight(1f).height(12.dp).clip(CircleShape),
                                            color = NeonCyan,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${day.calls}", color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                    
                    item {
                        Text("Team Leaderboard", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    }
                    
                    items(data.rankings) { rank ->
                        GlassCard {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(CircleShape).background(DeepSpaceBlack),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(rank.name.first().toString(), color = ElectricIndigo, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(rank.name, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Progress: ${(rank.targetProgress * 100).toInt()}%", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${rank.callsToday} Calls", fontWeight = FontWeight.Bold, color = NeonCyan)
                                    Icon(
                                        imageVector = if (rank.trend == "UP") Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = rank.trend,
                                        tint = if (rank.trend == "UP") EmeraldSuccess else RoseDanger,
                                        modifier = Modifier.size(16.dp)
                                    )
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
