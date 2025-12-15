package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.Material
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DeleteMaterialUiState {
    object Loading : DeleteMaterialUiState()
    data class Success(val material: Material) : DeleteMaterialUiState()
    data class Error(val message: String) : DeleteMaterialUiState()
    object Deleted : DeleteMaterialUiState()
}

class DeleteMaterialViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DeleteMaterialUiState>(DeleteMaterialUiState.Loading)
    val uiState: StateFlow<DeleteMaterialUiState> = _uiState

    fun getMaterial(token: String, materialId: Int) {
        viewModelScope.launch {
            _uiState.value = DeleteMaterialUiState.Loading
            try {
                val response = RetrofitClient.apiService.getMaterial("Bearer $token", materialId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _uiState.value = DeleteMaterialUiState.Success(it)
                    } ?: run {
                        _uiState.value = DeleteMaterialUiState.Error("Material no encontrado")
                    }
                } else {
                    _uiState.value = DeleteMaterialUiState.Error("Error al obtener el material")
                }
            } catch (e: Exception) {
                _uiState.value = DeleteMaterialUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun deleteMaterial(token: String, materialId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteMaterial("Bearer $token", materialId)
                if (response.isSuccessful) {
                    _uiState.value = DeleteMaterialUiState.Deleted
                } else {
                    _uiState.value = DeleteMaterialUiState.Error("Error al eliminar el material")
                }
            } catch (e: Exception) {
                _uiState.value = DeleteMaterialUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
