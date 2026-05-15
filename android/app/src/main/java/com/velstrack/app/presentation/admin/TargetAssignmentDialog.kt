package com.velstrack.app.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.velstrack.app.data.remote.dto.EmployeeDto
import com.velstrack.app.data.remote.dto.CreateTargetRequest
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetAssignmentDialog(
    employees: List<EmployeeDto>,
    onDismiss: () -> Unit,
    onSubmit: (CreateTargetRequest) -> Unit
) {
    var selectedEmployeeId by remember { mutableStateOf("TEAM") }
    var targetType by remember { mutableStateOf("DAILY") }
    var targetValue by remember { mutableStateOf("") }
    
    var expandedEmployee by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign Target") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Employee Dropdown
                val options = listOf(EmployeeDto("TEAM", "Whole Team (Divide Target)", "", "", "", false, "", "")) + employees
                
                ExposedDropdownMenuBox(
                    expanded = expandedEmployee,
                    onExpandedChange = { expandedEmployee = !expandedEmployee }
                ) {
                    val selectedName = options.find { it._id == selectedEmployeeId }?.name ?: "Select Employee"
                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Employee / Team") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEmployee) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEmployee,
                        onDismissRequest = { expandedEmployee = false }
                    ) {
                        options.forEach { emp ->
                            DropdownMenuItem(
                                text = { Text(emp.name) },
                                onClick = {
                                    selectedEmployeeId = emp._id
                                    expandedEmployee = false
                                }
                            )
                        }
                    }
                }

                // Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        value = targetType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        listOf("DAILY", "WEEKLY", "MONTHLY").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    targetType = type
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = targetValue,
                    onValueChange = { targetValue = it },
                    label = { Text(if (selectedEmployeeId == "TEAM") "Total Target Value (To be divided)" else "Target Value (Calls)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val value = targetValue.toIntOrNull() ?: 0
                if (value > 0 && selectedEmployeeId.isNotEmpty()) {
                    val now = LocalDateTime.now()
                    val start = now.format(DateTimeFormatter.ISO_DATE_TIME)
                    val end = now.plusDays(if (targetType == "DAILY") 1 else if (targetType == "WEEKLY") 7 else 30).format(DateTimeFormatter.ISO_DATE_TIME)
                    
                    onSubmit(
                        CreateTargetRequest(
                            employeeId = selectedEmployeeId,
                            targetType = targetType,
                            targetValue = value,
                            periodStart = start,
                            periodEnd = end
                        )
                    )
                }
            }) {
                Text("Assign")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
