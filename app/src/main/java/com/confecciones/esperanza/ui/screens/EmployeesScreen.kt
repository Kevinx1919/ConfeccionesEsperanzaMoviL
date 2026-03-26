package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.models.Employee
import com.confecciones.esperanza.ui.theme.*
import com.confecciones.esperanza.viewmodels.EmployeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: EmployeeViewModel = viewModel()
) {
    val filteredEmployees by viewModel.filteredEmployees.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getEmployees()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Empleados", fontWeight = FontWeight.SemiBold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Outlined.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBackground)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholderText = "Buscar empleado",
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
                    filteredEmployees.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No se encontraron empleados", color = AppTextSecondary, fontSize = 16.sp)
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = AppSpacing.md, end = AppSpacing.md, bottom = AppSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                        ) {
                            items(filteredEmployees) { employee ->
                                EmployeeCard(employee = employee, onClick = { onNavigateToDetail(employee.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeCard(employee: Employee, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm),
        shape = RoundedCornerShape(AppRadius.lg)
    ) {
        Row(modifier = Modifier.padding(AppSpacing.md), verticalAlignment = Alignment.CenterVertically) {
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
                    text = employee.userName.firstOrNull()?.uppercase() ?: "E",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.userName,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTextPrimary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(text = employee.email, fontSize = 13.sp, color = AppTextSecondary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = employee.phoneNumber ?: "Sin telefono", fontSize = 13.sp, color = AppTextSecondary)
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = if (employee.lockoutEnabled) Color(0xFFFEE2E2) else Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(AppRadius.sm)
                ) {
                    Text(
                        text = if (employee.lockoutEnabled) "Bloqueado" else "Activo",
                        modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (employee.lockoutEnabled) Color(0xFF991B1B) else Color(0xFF065F46)
                    )
                }
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                val role = employee.roles?.firstOrNull() ?: "Sin rol"
                Surface(
                    color = Color(0xFFE0E7FF),
                    shape = RoundedCornerShape(AppRadius.sm)
                ) {
                    Text(
                        text = role,
                        modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3730A3)
                    )
                }
            }
        }
    }
}
