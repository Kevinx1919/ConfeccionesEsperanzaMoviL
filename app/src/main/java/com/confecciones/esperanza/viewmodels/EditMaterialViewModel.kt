package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.Color
import com.confecciones.esperanza.models.CreateMaterialRequest
import com.confecciones.esperanza.models.Material
import com.confecciones.esperanza.models.TipoMaterial
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class EditMaterialUiState {
    object Idle : EditMaterialUiState()
    object Loading : EditMaterialUiState()
    object Success : EditMaterialUiState()
    data class Error(val message: String) : EditMaterialUiState()
}

class EditMaterialViewModel : ViewModel() {

    // Form state
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _cantidad = MutableStateFlow("1")
    val cantidad: StateFlow<String> = _cantidad.asStateFlow()

    private val _proveedor = MutableStateFlow("")
    val proveedor: StateFlow<String> = _proveedor.asStateFlow()

    private val _selectedTipoMaterial = MutableStateFlow<TipoMaterial?>(null)
    val selectedTipoMaterial: StateFlow<TipoMaterial?> = _selectedTipoMaterial.asStateFlow()

    private val _selectedColor = MutableStateFlow<Color?>(null)
    val selectedColor: StateFlow<Color?> = _selectedColor.asStateFlow()

    // Dropdown lists
    private val _tiposMaterial = MutableStateFlow<List<TipoMaterial>>(emptyList())
    val tiposMaterial: StateFlow<List<TipoMaterial>> = _tiposMaterial.asStateFlow()

    private val _colores = MutableStateFlow<List<Color>>(emptyList())
    val colores: StateFlow<List<Color>> = _colores.asStateFlow()

    // UI State
    private val _uiState = MutableStateFlow<EditMaterialUiState>(EditMaterialUiState.Idle)
    val uiState: StateFlow<EditMaterialUiState> = _uiState.asStateFlow()

    private val _formError = MutableStateFlow<String?>(null)
    val formError: StateFlow<String?> = _formError.asStateFlow()

    fun onNombreChange(value: String) {
        _nombre.value = value
    }

    fun onProveedorChange(value: String) {
        _proveedor.value = value
    }

    fun onCantidadChange(value: String) {
        _cantidad.value = value
    }

    fun onTipoMaterialSelected(tipo: TipoMaterial) {
        _selectedTipoMaterial.value = tipo
    }

    fun onColorSelected(color: Color) {
        _selectedColor.value = color
    }

    private fun getDefaultColors(): List<Color> {
        return listOf(
            Color(id = 1, nombre = "Blanco"),
            Color(id = 2, nombre = "Negro"),
            Color(id = 3, nombre = "Azul marino"),
            Color(id = 4, nombre = "Rojo"),
            Color(id = 5, nombre = "Verde"),
            Color(id = 6, nombre = "Amarillo"),
            Color(id = 7, nombre = "Rosa"),
            Color(id = 8, nombre = "Gris"),
            Color(id = 9, nombre = "Beige"),
            Color(id = 10, nombre = "Morado")
        )
    }

    fun loadMaterial(token: String, materialId: Int) {
        viewModelScope.launch {
            _uiState.value = EditMaterialUiState.Loading
            try {
                // Fetch Material
                val materialResponse = RetrofitClient.apiService.getMaterial("Bearer $token", materialId)
                if (materialResponse.isSuccessful) {
                    val material = materialResponse.body()!!
                    _nombre.value = material.nombre
                    _cantidad.value = material.cantidad.toInt().toString()
                    _proveedor.value = material.proveedor

                    // Fetch and set dropdowns
                    loadDropdowns(token, material)
                } else {
                    throw Exception("Error al cargar el material")
                }
            } catch (e: Exception) {
                _uiState.value = EditMaterialUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun loadDropdowns(token: String, material: Material) {
        viewModelScope.launch {
            try {
                // Fetch Tipos de Material
                val tiposResponse = RetrofitClient.apiService.getTiposMaterial("Bearer $token")
                if (tiposResponse.isSuccessful) {
                    val tipos = tiposResponse.body() ?: emptyList()
                    _tiposMaterial.value = tipos
                    _selectedTipoMaterial.value = tipos.find { it.id == material.tipoMaterialId }
                } else {
                    throw Exception("Error al cargar tipos de material")
                }

                // Fetch Colores
                _colores.value = try {
                    val coloresResponse = RetrofitClient.apiService.getColores("Bearer $token")
                    if (coloresResponse.isSuccessful && !coloresResponse.body().isNullOrEmpty()) {
                        val colores = coloresResponse.body()!!
                        _selectedColor.value = colores.find { it.id == material.colorId }
                        colores
                    } else {
                        getDefaultColors()
                    }
                } catch (e: Exception) {
                    getDefaultColors()
                }
                
                _uiState.value = EditMaterialUiState.Idle
            } catch (e: Exception) {
                _uiState.value = EditMaterialUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateMaterial(token: String, materialId: Int) {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.value = EditMaterialUiState.Loading
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val currentDate = sdf.format(Date())

                val request = CreateMaterialRequest(
                    nombre = _nombre.value,
                    cantidad = _cantidad.value.toInt(),
                    fechaEntrada = currentDate,
                    proveedor = _proveedor.value,
                    tipoMaterialId = _selectedTipoMaterial.value!!.id,
                    colorId = _selectedColor.value!!.id
                )

                val response = RetrofitClient.apiService.updateMaterial("Bearer $token", materialId, request)
                if (response.isSuccessful) {
                    _uiState.value = EditMaterialUiState.Success
                } else {
                    _uiState.value = EditMaterialUiState.Error("Error al actualizar el material: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = EditMaterialUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    private fun validateForm(): Boolean {
        val cantidadInt = _cantidad.value.toIntOrNull()
        return when {
            _nombre.value.isBlank() -> {
                _formError.value = "El nombre es obligatorio"
                false
            }
            _proveedor.value.isBlank() -> {
                _formError.value = "El proveedor es obligatorio"
                false
            }
            cantidadInt == null || cantidadInt <= 0 -> {
                _formError.value = "La cantidad debe ser un número mayor que cero"
                false
            }
            _selectedTipoMaterial.value == null -> {
                _formError.value = "Selecciona un tipo de material"
                false
            }
            _selectedColor.value == null -> {
                _formError.value = "Selecciona un color"
                false
            }
            else -> {
                _formError.value = null
                true
            }
        }
    }
}