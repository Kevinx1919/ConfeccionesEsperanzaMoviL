package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.models.Material
import com.confecciones.esperanza.viewmodels.StockViewModel

@Composable
fun StockScreen(
    token: String,
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StockViewModel = viewModel()
) {
    val filteredMateriales by viewModel.filteredMateriales.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(token) {
        viewModel.getMateriales(token)
    }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            placeholderText = "Buscar material...",
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF7C3AED))
            } else if (error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚠️", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(error!!, color = Color.Red, fontSize = 16.sp)
                }
            } else if (filteredMateriales.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📊", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No se encontraron materiales", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMateriales) { material ->
                        MaterialCard(material = material, onClick = { onNavigateToDetail(material.idMaterial) })
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialCard(
    material: Material,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(0xFF7C3AED).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = material.nombre.firstOrNull()?.uppercase() ?: "M",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C3AED)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Proveedor: ${material.proveedor}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${material.cantidad.toInt()} Unidades",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = material.fechaEntrada.split("T").first(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
