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
import com.confecciones.esperanza.models.Tarea
import com.confecciones.esperanza.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    token: String,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val tareas by viewModel.tareas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(token) {
        viewModel.getTareas(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas", fontWeight = FontWeight.Bold, color = Color.White) },
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
                Icon(Icons.Default.Add, "Agregar Tarea", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
        ) {
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
            } else if (tareas.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📋", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay tareas registradas", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tareas) { tarea ->
                        TaskCard(tarea = tarea, onClick = { onNavigateToDetail(tarea.idTarea) })
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    tarea: Tarea,
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tarea.nombreTarea,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tarea.descripcion,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Activas", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = tarea.asignacionesActivas.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Completadas", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = tarea.asignacionesCompletadas.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}
