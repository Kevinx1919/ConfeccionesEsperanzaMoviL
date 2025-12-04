package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.confecciones.esperanza.models.Cliente
import com.confecciones.esperanza.viewmodels.CustomerViewModel
import com.confecciones.esperanza.viewmodels.CustomerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteClienteScreen(
    clienteId: Int,
    token: String,
    onNavigateBack: () -> Unit,
    viewModel: CustomerViewModel = viewModel()
) {
    val clienteState by viewModel.clienteState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Cargar datos del cliente
    LaunchedEffect(clienteId) {
        viewModel.loadCliente(token, clienteId)
    }

    // Manejar éxito de eliminación
    LaunchedEffect(operationState) {
        if (operationState is CustomerUiState.OperationSuccess) {
            kotlinx.coroutines.delay(1500)
            viewModel.resetOperationState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eliminar Cliente", fontWeight = FontWeight.Bold, color = Color.White) },
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
                    DeleteClienteContent(
                        cliente = cliente,
                        operationState = operationState,
                        onConfirmDelete = { showConfirmDialog = true },
                        onCancel = onNavigateBack
                    )

                    // Diálogo de confirmación
                    if (showConfirmDialog) {
                        ConfirmDeleteDialog(
                            cliente = cliente,
                            onConfirm = {
                                showConfirmDialog = false
                                viewModel.deleteCliente(token, clienteId)
                            },
                            onDismiss = { showConfirmDialog = false }
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun DeleteClienteContent(
    cliente: Cliente,
    operationState: CustomerUiState,
    onConfirmDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mensajes
        when (operationState) {
            is CustomerUiState.OperationSuccess -> {
                SuccessMessage((operationState).message)
                Spacer(modifier = Modifier.height(16.dp))
            }
            is CustomerUiState.Error -> {
                ErrorMessage((operationState).message)
                Spacer(modifier = Modifier.height(16.dp))
            }
            else -> {}
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícono de advertencia
                Text(
                    text = "⚠️",
                    fontSize = 72.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Advertencia!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "El cliente se eliminará permanentemente.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¿Desea continuar?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFDC2626)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Datos del cliente a eliminar:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                ClienteInfoItem("ID:", cliente.idCliente.toString())
                ClienteInfoItem("Nombre:", cliente.nombreCompleto)
                ClienteInfoItem("Email:", cliente.emailCliente)
                ClienteInfoItem("Teléfono:", cliente.telefonoCliente)
                ClienteInfoItem("Documento:", cliente.numeroDocCliente)

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        enabled = operationState !is CustomerUiState.Loading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6B7280)
                        )
                    ) {
                        Text("NO, CANCELAR")
                    }

                    Button(
                        onClick = onConfirmDelete,
                        modifier = Modifier.weight(1f),
                        enabled = operationState !is CustomerUiState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC2626)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (operationState is CustomerUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("SÍ, ELIMINAR")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    cliente: Cliente,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(text = "⚠️", fontSize = 48.sp)
        },
        title = {
            Text(
                text = "Confirmación Final",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC2626)
            )
        },
        text = {
            Column {
                Text(
                    text = "Esta acción no se puede deshacer.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "¿Está completamente seguro de eliminar a ${cliente.nombreCompleto}?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626)
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF6B7280))
            }
        }
    )
}

@Composable
fun ClienteInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = Color(0xFF1F2937)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}