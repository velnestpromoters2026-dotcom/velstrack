package com.velstrack.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velstrack.app.core.datastore.SessionManager
import com.velstrack.app.data.remote.api.ApiService
import com.velstrack.app.data.remote.api.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.velstrack.app.domain.updater.AppUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val appUpdater: AppUpdater
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, pass: String) {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        
        if (trimmedEmail.isBlank() || trimmedPass.isBlank()) {
            _loginState.value = LoginState.Error("Please enter email and password")
            return
        }
        
        _loginState.value = LoginState.Loading
        
        viewModelScope.launch {
            try {
                val request = LoginRequest(email = trimmedEmail, password = trimmedPass)
                val response = apiService.login(request)
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success && apiResponse.data != null) {
                        val body = apiResponse.data
                        sessionManager.saveSession(body.token, body.role, body._id)
                        _loginState.value = LoginState.Success(body.role)
                    } else {
                        _loginState.value = LoginState.Error(apiResponse?.message ?: "Login failed")
                    }
                } else {
                    _loginState.value = LoginState.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loginState.value = LoginState.Error("Error: ${e.message ?: "Unknown network error"}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                apiService.logout()
            } catch (e: Exception) {
                // Ignore network error on logout
            } finally {
                sessionManager.clearSession()
                _loginState.value = LoginState.Idle
            }
        }
    }

    fun checkForUpdates(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = appUpdater.checkForUpdates()
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val role: String) : LoginState()
    data class Error(val error: String) : LoginState()
}
