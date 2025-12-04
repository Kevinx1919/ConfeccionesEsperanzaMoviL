package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.ProfileData
import com.confecciones.esperanza.models.ProfileUpdateRequest
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val profile: ProfileData) : ProfileUiState()
    data class UpdateSuccess(val message: String, val profile: ProfileData) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _profileData = MutableStateFlow<ProfileData?>(null)
    val profileData: StateFlow<ProfileData?> = _profileData

    fun fetchProfile(token: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            try {
                val response = RetrofitClient.apiService.getProfile("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    _profileData.value = profile
                    _uiState.value = ProfileUiState.Success(profile)
                } else {
                    _uiState.value = ProfileUiState.Error("Error al cargar el perfil: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error de conexión: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun updateProfile(token: String, userName: String, email: String, phoneNumber: String?) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            try {
                val request = ProfileUpdateRequest(userName, email, phoneNumber)
                val response = RetrofitClient.apiService.updateProfile("Bearer $token", request)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    if (result.isSuccess && result.data != null) {
                        _profileData.value = result.data
                        _uiState.value = ProfileUiState.UpdateSuccess(result.message, result.data)
                    } else {
                        _uiState.value = ProfileUiState.Error(result.message)
                    }
                } else {
                    _uiState.value = ProfileUiState.Error("Error al actualizar: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error de conexión: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun resetState() {
        _uiState.value = ProfileUiState.Idle
    }
}