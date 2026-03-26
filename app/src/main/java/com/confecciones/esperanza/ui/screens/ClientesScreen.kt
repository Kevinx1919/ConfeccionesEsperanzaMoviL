package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.models.Cliente
import com.confecciones.esperanza.ui.theme.*
import com.confecciones.esperanza.viewmodels.CustomerUiState
import com.confecciones.esperanza.viewmodels.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: CustomerViewModel = viewModel()
) {
    val clientesState by viewModel.clientesState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredClientes = remember(clientesState, searchQuery) { viewModel.getFilteredClientes() }

    LaunchedEffect(Unit) {
        viewModel.loadClientes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes", fontWeight = FontWeight.SemiBold, color = Color.White) },
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
                onQueryChange = { viewModel.setSearchQuery(it) },
                placeholderText = "Buscar cliente",
                modifier = Modifier.padding(AppSpacing.md)
            )

            when (clientesState) {
                is CustomerUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PurplePrimary)
                    }
                }

                is CustomerUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(AppSpacing.lg)) {
                            Text("Ocurrio un error", fontSize = 16.sp, color = AppError)
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                            Button(onClick = { viewModel.loadClientes() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                is CustomerUiState.Success<*> -> {
                    if (filteredClientes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (searchQuery.isBlank()) "No hay clientes registrados" else "No se encontraron clientes",
                                color = AppTextSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                        ) {
                            items(filteredClientes) { cliente ->
                                ClienteCard(cliente = cliente, onClick = { onNavigateToDetail(cliente.idCliente) })
                            }
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholderText, color = AppTextMuted) },
        leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = PurplePrimary)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Outlined.Clear, contentDescription = null, tint = AppTextMuted)
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppRadius.md),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
        singleLine = true
    )
}

@Composable
fun ClienteCard(
    cliente: Cliente,
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
                    text = cliente.nombreCliente.firstOrNull()?.uppercase() ?: "C",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cliente.nombreCompleto,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTextPrimary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = AppTextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(AppSpacing.xs))
                    Text(text = cliente.emailCliente, fontSize = 13.sp, color = AppTextSecondary)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Phone, contentDescription = null, tint = AppTextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(AppSpacing.xs))
                    Text(text = cliente.telefonoCliente, fontSize = 13.sp, color = AppTextSecondary)
                }
            }

            Surface(
                color = PurplePrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(AppRadius.sm)
            ) {
                Text(
                    text = "${cliente.totalPedidos} pedidos",
                    modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PurplePrimary
                )
            }
        }
    }
}
