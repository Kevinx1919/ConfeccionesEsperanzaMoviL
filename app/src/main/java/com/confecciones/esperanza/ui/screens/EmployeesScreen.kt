package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.confecciones.esperanza.viewmodels.EmployeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(
    token: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: EmployeeViewModel = viewModel()
) {
    val filteredEmployees by viewModel.filteredEmployees.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(token) {
        viewModel.getEmployees(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Empleados", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7C3AED))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Color(0xFF7C3AED)
            ) {
                Icon(Icons.Default.Add, "Agregar Empleado", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholderText = "Buscar empleado...",
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
                } else if (filteredEmployees.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("👥", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No se encontraron empleados", color = Color.Gray, fontSize = 16.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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

@Composable
fun EmployeeCard(employee: Employee, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Avatar
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
                    text = employee.userName.firstOrNull()?.uppercase() ?: "E",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C3AED)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = employee.email,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = employee.phoneNumber ?: "Sin teléfono",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // Status Badge
            Column(horizontalAlignment = Alignment.End) {
                 Surface(
                    color = if (employee.lockoutEnabled) Color(0xFFFEE2E2) else Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (employee.lockoutEnabled) "Bloqueado" else "Activo",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (employee.lockoutEnabled) Color(0xFF991B1B) else Color(0xFF065F46)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                val role = employee.roles?.firstOrNull() ?: "Sin rol"
                 Surface(
                    color = Color(0xFFE0E7FF),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = role,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3730A3)
                    )
                }
            }
        }
    }
}