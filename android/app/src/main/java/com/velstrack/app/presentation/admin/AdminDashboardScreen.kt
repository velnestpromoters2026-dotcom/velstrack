package com.velstrack.app.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.velstrack.app.core.theme.RoseDanger
import com.velstrack.app.core.util.UiState
import com.velstrack.app.presentation.auth.AuthViewModel
import com.velstrack.app.presentation.components.*

@Composable
fun AdminOverviewTab(
    viewModel: AdminViewModel
) {
    val dashboardState by viewModel.dashboardState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (dashboardState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ElectricIndigo
                    )
                }
                is UiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Dashboard data is empty.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorStateCard(
                            message = (dashboardState as UiState.Error).message,
                            onRetry = { viewModel.loadDashboard() }
                        )
                    }
                }
                is UiState.Success -> {
                    val data = (dashboardState as UiState.Success).data
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            Column {
                                Text(
                                    text = "Analytics",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Executive performance metrics across all teams.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                KPIStatCard(
                                    title = "Total Active Staff",
                                    value = (data.activeEmployees ?: 0).toString(),
                                    trend = "${data.totalEmployees ?: 0} total registered",
                                    isPositive = true,
                                    icon = Icons.Default.Person,
                                    iconTint = ElectricIndigo,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                KPIStatCard(
                                    title = "Calls Synced",
                                    value = (data.totalCallsSynced ?: 0).toString(),
                                    trend = "Securely backed up",
                                    isPositive = true,
                                    icon = Icons.Default.List,
                                    iconTint = NeonCyan,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            SectionHeader(title = "System Health")
                            Spacer(modifier = Modifier.height(16.dp))
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                if (data.recentActivity.isNullOrEmpty()) {
                                    EmptyStateCard(
                                        title = "System is Quiet",
                                        message = "No recent system activity to display right now.",
                                        icon = Icons.Default.List
                                    )
                                } else {
                                    ActivityTimeline(
                                        items = data.recentActivity.mapIndexed { index, activity ->
                                            TimelineItem(
                                                title = activity.type,
                                                time = activity.timestamp,
                                                duration = activity.description,
                                                isHighlighted = index == 0,
                                                isSynced = false
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        item {
                            SectionHeader(title = "Team Management")
                            Spacer(modifier = Modifier.height(16.dp))
                            InsightBanner(message = "Manage employee access, track performance, and view call histories in the Team tab.")
                        }

                        item {
                            SectionHeader(title = "Meta Analytics")
                            Spacer(modifier = Modifier.height(16.dp))
                            InsightBanner(message = "Meta Ads reporting requires campaign tracking access. Check the Campaigns tab.")
                        }
                    }
                }
        }
    }
}
