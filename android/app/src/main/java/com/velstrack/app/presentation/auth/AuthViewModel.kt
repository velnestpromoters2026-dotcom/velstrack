package com.velstrack.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velstrack.app.core.datastore.SessionManager
import com.velstrack.app.data.remote.api.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginState.Error("Please enter email and password")
            return
        }
        
        _loginState.value = LoginState.Loading
        
        viewModelScope.launch {
            try {
                val request = mapOf("email" to email, "password" to pass)
                val response = apiService.login(request)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        sessionManager.saveSession(body.token, body.role)
                        _loginState.value = LoginState.Success(body.role)
                    } else {
                        _loginState.value = LoginState.Error("Empty response body")
                    }
                } else {
                    _loginState.value = LoginState.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Network Error: Could not connect to server")
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
