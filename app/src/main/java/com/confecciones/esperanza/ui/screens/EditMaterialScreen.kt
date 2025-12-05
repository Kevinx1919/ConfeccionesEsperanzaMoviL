package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.models.Color as ColorModel
import com.confecciones.esperanza.models.TipoMaterial
import com.confecciones.esperanza.viewmodels.EditMaterialViewModel
import com.confecciones.esperanza.viewmodels.EditMaterialUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMaterialScreen(
    materialId: Int,
    token: String,
    onNavigateBack: () -> Unit,
    viewModel: EditMaterialViewModel = viewModel()
) {
    val nombre by viewModel.nombre.collectAsState()
    val cantidad by viewModel.cantidad.collectAsState()
    val proveedor by viewModel.proveedor.collectAsState()
    val selectedTipoMaterial by viewModel.selectedTipoMaterial.collectAsState()
    val selectedColor by viewModel.selectedColor.collectAsState()
    val tiposMaterial by viewModel.tiposMaterial.collectAsState()
    val colores by viewModel.colores.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val formError by viewModel.formError.collectAsState()

    LaunchedEffect(materialId) {
        viewModel.loadMaterial(token, materialId)
    }

    LaunchedEffect(uiState) {
        if (uiState is EditMaterialUiState.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Material", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7C3AED))
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F8F8))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Información del Material", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = viewModel::onNombreChange,
                            label = { Text("Nombre *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = proveedor,
                            onValueChange = viewModel::onProveedorChange,
                            label = { Text("Proveedor *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Cantidad Counter
                        QuantityCounter(cantidad = cantidad, onCantidadChange = viewModel::onCantidadChange)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Tipo de Material Dropdown
                        MaterialDropdown(tiposMaterial, selectedTipoMaterial, viewModel::onTipoMaterialSelected, "Tipo de Material *")
                        Spacer(modifier = Modifier.height(16.dp))

                        // Color Dropdown
                        ColorDropdown(colores, selectedColor, viewModel::onColorSelected, "Color *")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (formError != null) {
                    Text(formError!!, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
                }

                // Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = onNavigateBack, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { viewModel.updateMaterial(token, materialId) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        enabled = uiState !is EditMaterialUiState.Loading
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }

            if (uiState is EditMaterialUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            (uiState as? EditMaterialUiState.Error)?.let {
                Snackbar(message = it.message, modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}
