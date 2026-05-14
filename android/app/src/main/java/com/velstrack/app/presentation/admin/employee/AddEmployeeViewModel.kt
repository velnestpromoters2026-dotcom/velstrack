package com.velstrack.app.presentation.admin.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velstrack.app.data.remote.dto.AddEmployeeRequest
import com.velstrack.app.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEmployeeViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _addState = MutableStateFlow<AddEmployeeState>(AddEmployeeState.Idle)
    val addState: StateFlow<AddEmployeeState> = _addState

    fun addEmployee(name: String, email: String, phone: String, pass: String, role: String) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()
        
        if (trimmedName.isBlank() || trimmedEmail.isBlank() || pass.isBlank()) {
            _addState.value = AddEmployeeState.Error("Please fill all required fields")
            return
        }

        _addState.value = AddEmployeeState.Loading
        viewModelScope.launch {
            val request = AddEmployeeRequest(
                name = trimmedName,
                email = trimmedEmail,
                phone = trimmedPhone,
                password = pass,
                role = role,
                department = "Sales"
            )
            repository.addEmployee(request).collect { result ->
                if (result.isSuccess) {
                    _addState.value = AddEmployeeState.Success
                } else {
                    _addState.value = AddEmployeeState.Error(result.exceptionOrNull()?.message ?: "Failed to add employee")
                }
            }
        }
    }
}

sealed class AddEmployeeState {
    object Idle : AddEmployeeState()
    object Loading : AddEmployeeState()
    object Success : AddEmployeeState()
    data class Error(val error: String) : AddEmployeeState()
}
