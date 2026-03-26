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
import com.confecciones.esperanza.models.Tarea
import com.confecciones.esperanza.ui.theme.*
import com.confecciones.esperanza.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val tareas by viewModel.tareas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getTareas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas", fontWeight = FontWeight.SemiBold, color = Color.White) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBackground)
        ) {
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
                tareas.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No hay tareas registradas", color = AppTextSecondary, fontSize = 16.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(AppSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                    ) {
                        items(tareas) { tarea ->
                            TaskCard(tarea = tarea, onClick = { onNavigateToDetail(tarea.idTarea) })
                        }
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
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm),
        shape = RoundedCornerShape(AppRadius.lg)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = tarea.nombreTarea,
                fontWeight = FontWeight.SemiBold,
                color = AppTextPrimary
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = tarea.descripcion,
                fontSize = 14.sp,
                color = AppTextSecondary,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Activas", fontSize = 12.sp, color = AppTextMuted)
                    Text(
                        text = tarea.asignacionesActivas.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppWarning
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Completadas", fontSize = 12.sp, color = AppTextMuted)
                    Text(
                        text = tarea.asignacionesCompletadas.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppSuccess
                    )
                }
            }
        }
    }
}
