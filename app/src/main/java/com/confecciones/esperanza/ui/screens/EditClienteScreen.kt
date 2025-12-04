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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.models.Cliente
import com.confecciones.esperanza.models.ClienteRequest
import com.confecciones.esperanza.viewmodels.CustomerViewModel
import com.confecciones.esperanza.viewmodels.CustomerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClienteScreen(
    clienteId: Int,
    token: String,
    onNavigateBack: () -> Unit,
    viewModel: CustomerViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var documento by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var isLoaded by remember { mutableStateOf(false) }

    val clienteState by viewModel.clienteState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()

    // Cargar datos del cliente
    LaunchedEffect(clienteId) {
        viewModel.loadCliente(token, clienteId)
    }

    // Rellenar formulario cuando se cargue el cliente
    LaunchedEffect(clienteState) {
        if (clienteState is CustomerUiState.Success<*> && !isLoaded) {
            val cliente = (clienteState as CustomerUiState.Success<*>).data as Cliente
            nombre = cliente.nombreCliente
            apellido = cliente.apellidoCliente
            email = cliente.emailCliente
            telefono = cliente.telefonoCliente
            documento = cliente.numeroDocCliente
            direccion = cliente.direccionCliente
            codigoPostal = cliente.codigoPostalCliente
            isLoaded = true
        }
    }

    // Manejar éxito
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
                title = { Text("Editar Cliente", fontWeight = FontWeight.Bold, color = Color.White) },
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
            when (clienteState) {
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
                            Text((clienteState as CustomerUiState.Error).message, color = Color(0xFFDC2626))
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Mensajes
                        when (operationState) {
                            is CustomerUiState.OperationSuccess -> {
                                SuccessMessage((operationState as CustomerUiState.OperationSuccess).message)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            is CustomerUiState.Error -> {
                                ErrorMessage((operationState as CustomerUiState.Error).message)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            else -> {}
                        }

                        // Formulario
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "Información del Cliente",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedTextField(
                                    value = nombre,
                                    onValueChange = { nombre = it },
                                    label = { Text("Nombre *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = operationState !is CustomerUiState.Loading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF7C3AED),
                                        focusedLabelColor = Color(0xFF7C3AED)
                                    )
                                )

                                OutlinedTextField(
                                    value = apellido,
                                    onValueChange = { apellido = it },
                                    label = { Text("Apellido *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = operationState !is CustomerUiState.Loading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF7C3AED),
                                        focusedLabelColor = Color(0xFF7C3AED)
                                    )
                                )

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email *") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = operationState !is CustomerUiState.Loading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF7C3AED),
                                        focusedLabelColor = Color(0xFF7C3AED)
                                    )
                                )

                                OutlinedTextField(
                                    value = telefono,
                                    onValueChange = { telefono = it },
                                    label = { Text("Teléfono *") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = operationState !is CustomerUiState.Loading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF7C3AED),
                                        focusedLabelColor = Color(0xFF7C3AED)
                                    )
                                )

                                OutlinedTextField(
                                    value = documento,
                                    onValueChange = { documento = it },
                                    label = { Text("Número de Documento *") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = operationState !is CustomerUiState.Loading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF7C3AED),
                                        focusedLabelColor = Color(0xFF7C3AED)
                                    )
                                )

                                OutlinedTextField(
                                    value = direccion,
                                    onValueChange = { direccion = it },
                                    label = { Text("Dirección *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = operationState !is CustomerUiState.Loading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF7C3AED),
                                        focusedLabelColor = Color(0xFF7C3AED)
                                    )
                                )

                                OutlinedTextField(
                                    value = codigoPostal,
                                    onValueChange = { codigoPostal = it },
                                    label = { Text("Código Postal *") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = operationState !is CustomerUiState.Loading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF7C3AED),
                                        focusedLabelColor = Color(0xFF7C3AED)
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = onNavigateBack,
                                        modifier = Modifier.weight(1f),
                                        enabled = operationState !is CustomerUiState.Loading,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Cancelar")
                                    }

                                    Button(
                                        onClick = {
                                            if (nombre.isNotBlank() && apellido.isNotBlank() &&
                                                email.isNotBlank() && telefono.isNotBlank() &&
                                                documento.isNotBlank() && direccion.isNotBlank() &&
                                                codigoPostal.isNotBlank()) {

                                                val request = ClienteRequest(
                                                    nombreCliente = nombre,
                                                    apellidoCliente = apellido,
                                                    emailCliente = email,
                                                    telefonoCliente = telefono,
                                                    numeroDocCliente = documento,
                                                    direccionCliente = direccion,
                                                    codigoPostalCliente = codigoPostal
                                                )
                                                viewModel.updateCliente(token, clienteId, request)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        enabled = operationState !is CustomerUiState.Loading &&
                                                nombre.isNotBlank() && apellido.isNotBlank() &&
                                                email.isNotBlank() && telefono.isNotBlank() &&
                                                documento.isNotBlank() && direccion.isNotBlank() &&
                                                codigoPostal.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFF59E0B)
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
                                            Text("Actualizar Cliente")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}