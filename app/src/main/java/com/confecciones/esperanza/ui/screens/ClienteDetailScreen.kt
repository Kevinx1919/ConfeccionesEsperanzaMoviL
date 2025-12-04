package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.models.Cliente
import com.confecciones.esperanza.viewmodels.CustomerViewModel
import com.confecciones.esperanza.viewmodels.CustomerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDetailScreen(
    clienteId: Int,
    token: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToDelete: (Int) -> Unit,
    viewModel: CustomerViewModel = viewModel()
) {
    val clienteState by viewModel.clienteState.collectAsState()

    LaunchedEffect(clienteId) {
        viewModel.loadCliente(token, clienteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Cliente", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
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
                .background(Color(0xFFF8F8F8))
        ) {
            when (val state = clienteState) {
                is CustomerUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF7C3AED))
                    }
                }

                is CustomerUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            Text("⚠️", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(state.message, color = Color(0xFFDC2626), fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadCliente(token, clienteId) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                is CustomerUiState.Success<*> -> {
                    val cliente = state.data as Cliente
                    ClienteDetailContent(
                        cliente = cliente,
                        onEdit = { onNavigateToEdit(cliente.idCliente) },
                        onDelete = { onNavigateToDelete(cliente.idCliente) }
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun ClienteDetailContent(
    cliente: Cliente,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header con avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF7C3AED), Color(0xFFEC4899))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cliente.nombreCliente.firstOrNull()?.uppercase() ?: "C",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = cliente.nombreCompleto,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "ID: ${cliente.idCliente}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, "Editar", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar")
            }

            Button(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, "Eliminar", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar")
            }
        }

        // Información del cliente
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Información Personal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                ClienteInfoRow(icon = "📧", label = "Email", value = cliente.emailCliente)
                ClienteInfoRow(icon = "📱", label = "Teléfono", value = cliente.telefonoCliente)
                ClienteInfoRow(icon = "🆔", label = "Documento", value = cliente.numeroDocCliente)
                ClienteInfoRow(icon = "📍", label = "Dirección", value = cliente.direccionCliente)
                ClienteInfoRow(icon = "📮", label = "Código Postal", value = cliente.codigoPostalCliente)

                Divider()

                ClienteInfoRow(icon = "📦", label = "Total Pedidos", value = cliente.totalPedidos.toString())
                ClienteInfoRow(icon = "📅", label = "Fecha Registro", value = cliente.fechaCreacion.take(10))
            }
        }
    }
}

@Composable
fun ClienteInfoRow(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color(0xFF1F2937)
            )
        }
    }
}