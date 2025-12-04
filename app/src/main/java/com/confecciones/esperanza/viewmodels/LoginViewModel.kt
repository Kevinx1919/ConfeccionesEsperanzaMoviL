package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.LoginRequest
import com.confecciones.esperanza.models.LoginResponse
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val response: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String, rememberMe: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            try {
                val request = LoginRequest(email, password, rememberMe)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    if (loginResponse.isSuccess) {
                        _uiState.value = LoginUiState.Success(loginResponse)
                    } else {
                        _uiState.value = LoginUiState.Error(loginResponse.message)
                    }
                } else {
                    _uiState.value = LoginUiState.Error("Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}