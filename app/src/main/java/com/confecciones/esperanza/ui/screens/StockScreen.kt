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
import com.confecciones.esperanza.ui.theme.*
import com.confecciones.esperanza.viewmodels.StockViewModel

@Composable
fun StockScreen(
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StockViewModel = viewModel()
) {
    val filteredMateriales by viewModel.filteredMateriales.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMateriales()
    }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            placeholderText = "Buscar material",
            modifier = Modifier.padding(AppSpacing.md)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PurplePrimary)
                }
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ocurrio un error", fontSize = 16.sp, color = AppError)
                    }
                }
                filteredMateriales.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No se encontraron materiales", color = AppTextSecondary, fontSize = 16.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = AppSpacing.md, end = AppSpacing.md, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                    ) {
                        items(filteredMateriales) { material ->
                            MaterialCard(material = material, onClick = { onNavigateToDetail(material.idMaterial) })
                        }
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
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm),
        shape = RoundedCornerShape(AppRadius.lg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = PurplePrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(AppRadius.md)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = material.nombre.firstOrNull()?.uppercase() ?: "M",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.nombre,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTextPrimary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = "Proveedor: ${material.proveedor}",
                    fontSize = 13.sp,
                    color = AppTextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${material.cantidad.toInt()} unidades",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTextPrimary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = material.fechaEntrada.split("T").first(),
                    fontSize = 12.sp,
                    color = AppTextMuted
                )
            }
        }
    }
}
