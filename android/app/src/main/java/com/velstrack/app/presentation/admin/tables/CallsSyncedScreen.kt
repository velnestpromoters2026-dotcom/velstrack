package com.velstrack.app.presentation.admin.tables

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.velstrack.app.core.theme.*
import com.velstrack.app.core.util.UiState
import com.velstrack.app.presentation.admin.AdminViewModel
import com.velstrack.app.data.remote.dto.CallLogDto
import com.velstrack.app.presentation.components.ErrorStateCard
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsSyncedScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val callsState by viewModel.callsState.collectAsState()
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf("All Time") }
    var showFilterMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCalls()
    }

    val filters = listOf("All Time", "Today", "This Week", "This Month")

    val filteredCalls = remember(callsState, selectedFilter) {
        if (callsState is UiState.Success) {
            val allCalls = (callsState as UiState.Success).data
            val now = Calendar.getInstance()
            allCalls.filter { call ->
                if (selectedFilter == "All Time") return@filter true
                
                try {
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    format.timeZone = TimeZone.getTimeZone("UTC")
                    val callDate = format.parse(call.timestamp) ?: return@filter true
                    val callCal = Calendar.getInstance().apply { time = callDate }

                    when (selectedFilter) {
                        "Today" -> {
                            callCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            callCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                        }
                        "This Week" -> {
                            callCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            callCal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
                        }
                        "This Month" -> {
                            callCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            callCal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        }
                        else -> true
                    }
                } catch (e: Exception) {
                    true
                }
            }
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calls Synced", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PureWhite)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Filter", tint = PureWhite)
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            filters.forEach { filter ->
                                DropdownMenuItem(
                                    text = { Text(filter, fontWeight = if (filter == selectedFilter) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedFilter = filter
                                        showFilterMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AbsoluteBlack,
                    titleContentColor = PureWhite
                )
            )
        },
        floatingActionButton = {
            if (filteredCalls.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { exportToCsv(context, filteredCalls) },
                    containerColor = PureWhite,
                    contentColor = AbsoluteBlack,
                    icon = { Icon(Icons.Default.Share, contentDescription = "Export") },
                    text = { Text("Export to Excel", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AbsoluteBlack)
                .padding(paddingValues)
        ) {
            when (callsState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PureWhite)
                }
                is UiState.Error -> {
                    ErrorStateCard(
                        message = (callsState as UiState.Error).message,
                        onRetry = { viewModel.loadCalls() }
                    )
                }
                is UiState.Empty, is UiState.Success -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Showing: $selectedFilter (${filteredCalls.size} records)",
                                color = MetallicSilver,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceGray)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Employee", color = PureWhite, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Text("Phone", color = PureWhite, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Text("Dur.", color = PureWhite, modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
                        }
                        
                        if (filteredCalls.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No call records found for this period.", color = MetallicSilver)
                            }
                        } else {
                            LazyColumn {
                                items(filteredCalls) { call ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp, horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(call.employeeName, color = PureWhite, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                        Text(call.clientPhone, color = MetallicSilver, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                        Text("${call.durationSeconds}s", color = PureWhite, modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Divider(color = SurfaceGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun exportToCsv(context: Context, calls: List<CallLogDto>) {
    val csvHeader = "Employee Name,Client Phone,Duration (Seconds),Date\n"
    val csvData = calls.joinToString("\n") { 
        "${it.employeeName},${it.clientPhone},${it.durationSeconds},${it.timestamp}" 
    }
    
    val file = File(context.cacheDir, "Calls_Export_${System.currentTimeMillis()}.csv")
    file.writeText(csvHeader + csvData)

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Export Calls to Excel"))
}
