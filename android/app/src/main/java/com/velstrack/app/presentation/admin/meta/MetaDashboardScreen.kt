package com.velstrack.app.presentation.admin.meta

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.velstrack.app.core.theme.DeepSpaceBlack
import com.velstrack.app.core.theme.ElectricIndigo
import com.velstrack.app.core.theme.NeonCyan
import com.velstrack.app.core.util.UiState
import com.velstrack.app.presentation.admin.AdminViewModel
import com.velstrack.app.presentation.components.EmptyStateCard
import com.velstrack.app.presentation.components.ErrorStateCard
import com.velstrack.app.presentation.components.GlassCard
import com.velstrack.app.presentation.components.KPIStatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaDashboardScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.metaState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMetaCampaigns()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meta Ads Analytics", fontWeight = FontWeight.Bold) },
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
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = NeonCyan)
                }
                is UiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyStateCard(
                            title = "No Active Campaign Data",
                            message = "Campaign analytics will appear once Meta Ads campaigns begin delivering impressions.",
                            icon = Icons.Default.Info,
                            actionText = "Link Account",
                            onActionClick = { /* Handle linking in future */ }
                        )
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorStateCard(
                            message = (state as UiState.Error).message,
                            onRetry = { viewModel.loadMetaCampaigns() }
                        )
                    }
                }
                is UiState.Success -> {
                    val campaigns = (state as UiState.Success).data
                    val totalSpend = campaigns.sumOf { it.spend }
                    val totalReach = campaigns.sumOf { it.reach }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                KPIStatCard(
                                    title = "Total Ad Spend",
                                    value = "$${String.format("%.2f", totalSpend)}",
                                    trend = "This Month",
                                    isPositive = false,
                                    icon = Icons.Default.Info,
                                    iconTint = NeonCyan,
                                    modifier = Modifier.weight(1f)
                                )
                                KPIStatCard(
                                    title = "Total Reach",
                                    value = "$totalReach",
                                    trend = "Unique Users",
                                    isPositive = true,
                                    icon = Icons.Default.Info,
                                    iconTint = ElectricIndigo,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        
                        item {
                            Text("Active Campaigns", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 8.dp))
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
                else -> Unit
            }
        }
    }
}
