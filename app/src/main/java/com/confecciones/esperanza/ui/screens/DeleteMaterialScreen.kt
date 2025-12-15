package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.viewmodels.DeleteMaterialViewModel
import com.confecciones.esperanza.viewmodels.DeleteMaterialUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteMaterialScreen(
    materialId: Int,
    token: String,
    onNavigateBack: () -> Unit,
    viewModel: DeleteMaterialViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(materialId) {
        viewModel.getMaterial(token, materialId)
    }

    LaunchedEffect(uiState) {
        if (uiState is DeleteMaterialUiState.Deleted) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eliminar Material", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7C3AED))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8)),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is DeleteMaterialUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is DeleteMaterialUiState.Success -> {
                    val material = state.material
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚠️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("¡Advertencia!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("El material se eliminará permanentemente.", textAlign = TextAlign.Center, color = Color.Gray)
                            Text("¿Desea continuar?", textAlign = TextAlign.Center, color = Color.Gray)
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Datos del material a eliminar:", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(12.dp))

                            InfoRow(label = "ID:", value = material.idMaterial.toString())
                            InfoRow(label = "Nombre:", value = material.nombre)
                            InfoRow(label = "Proveedor:", value = material.proveedor)
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                OutlinedButton(
                                    onClick = onNavigateBack,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("NO, CANCELAR")
                                }
                                Button(
                                    onClick = { viewModel.deleteMaterial(token, materialId) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                                ) {
                                    Text("SÍ, ELIMINAR")
                                }
                            }
                        }
                    }
                }
                is DeleteMaterialUiState.Error -> {
                    Text(state.message, color = Color.Red)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Text(text = value)
    }
}
