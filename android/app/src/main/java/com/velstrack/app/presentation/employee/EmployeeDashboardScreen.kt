package com.velstrack.app.presentation.employee

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.velstrack.app.core.theme.DeepSpaceBlack
import com.velstrack.app.core.theme.NeonCyan
import com.velstrack.app.core.theme.RoseDanger
import com.velstrack.app.core.util.UiState
import com.velstrack.app.presentation.auth.AuthViewModel
import com.velstrack.app.presentation.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDashboardScreen(
    onLogout: () -> Unit,
    onNavigateToDialer: () -> Unit,
    viewModel: EmployeeDashboardViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.startCallSyncWorker()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
        } else {
            viewModel.startCallSyncWorker()
            coroutineScope.launch {
                delay(1500) // Wait for worker
                viewModel.loadDashboard()
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentViewModel by rememberUpdatedState(viewModel)
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (hasPermission) {
                    currentViewModel.startCallSyncWorker()
                    coroutineScope.launch {
                        delay(2000) // Wait for Android CallLog to update and worker to sync
                        currentViewModel.loadDashboard()
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val dashboardState by viewModel.dashboardState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workspace", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSpaceBlack,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = RoseDanger
                ),
                actions = {
                    if (hasPermission) {
                        IconButton(onClick = {
                            viewModel.startCallSyncWorker()
                            coroutineScope.launch {
                                delay(1500)
                                viewModel.loadDashboard()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = NeonCyan)
                        }
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            if (hasPermission) {
                FloatingActionButton(
                    onClick = onNavigateToDialer,
                    containerColor = NeonCyan,
                    contentColor = DeepSpaceBlack
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Open Dialer")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepSpaceBlack)
                .padding(paddingValues)
        ) {
            if (!hasPermission) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyStateCard(
                        title = "Permission Required",
                        message = "Velstrack needs access to your call logs to automatically track and sync your sales calls.",
                        icon = Icons.Default.Call,
                        actionText = "Grant Permission",
                        onActionClick = { permissionLauncher.launch(Manifest.permission.READ_CALL_LOG) }
                    )
                }
            } else {
                when (dashboardState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NeonCyan
                    )
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
                                    text = "Overview",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Your daily performance & active tasks.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        item {
                            InsightBanner(message = "Background Sync is active. Calls are securely syncing to Velstrack servers.")
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                KPIStatCard(
                                    title = "Calls Today",
                                    value = (data.callsToday ?: 0).toString(),
                                    trend = data.callsTrend ?: "",
                                    isPositive = true,
                                    icon = Icons.Default.Call,
                                    iconTint = NeonCyan,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                GlassCard(modifier = Modifier.weight(1f)) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Target (${data.target ?: 0})",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        val target = data.target ?: 0
                                        val callsToday = data.callsToday ?: 0
                                        val progress = if (target > 0) callsToday.toFloat() / target else 0f
                                        ProgressRing(progress = progress, size = 80.dp, strokeWidth = 8.dp)
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(title = "Recent Activity", actionText = "View All", onActionClick = {})
                            Spacer(modifier = Modifier.height(16.dp))
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                if (data.recentCalls.isNullOrEmpty()) {
                                    EmptyStateCard(
                                        title = "No Recent Calls",
                                        message = "Your call activity will appear here once you start syncing calls.",
                                        icon = Icons.Default.Call
                                    )
                                } else {
                                    ActivityTimeline(
                                        items = data.recentCalls.mapIndexed { index, call ->
                                            TimelineItem(
                                                title = "Call with ${call.contactName}",
                                                subtitle = "Duration: ${call.duration}",
                                                time = call.timestamp,
                                                isHighlighted = index == 0
                                            )
                                        }
                                    )
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
}
