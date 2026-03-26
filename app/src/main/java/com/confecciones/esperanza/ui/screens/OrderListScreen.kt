package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.confecciones.esperanza.models.Order
import com.confecciones.esperanza.ui.theme.AppBackground
import com.confecciones.esperanza.ui.theme.AppRadius
import com.confecciones.esperanza.ui.theme.AppSpacing
import com.confecciones.esperanza.ui.theme.PurplePrimary
import com.confecciones.esperanza.viewmodels.OrderViewModel
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    orderViewModel: OrderViewModel = viewModel(),
    onNavigateBack: (() -> Unit)? = null,
    onNavigateToDetail: (Int) -> Unit
) {
    LaunchedEffect(Unit) { orderViewModel.loadOrders() }

    val filteredOrders by orderViewModel.filteredOrders.collectAsState()
    val searchQuery by orderViewModel.searchQuery.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    val error by orderViewModel.error.collectAsState()

    Scaffold(
        topBar = {
            if (onNavigateBack != null) {
                TopAppBar(
                    title = { Text("Pedidos", fontWeight = FontWeight.SemiBold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(paddingValues)
        ) {
            SearchBar(searchQuery, orderViewModel::onSearchQueryChange)
            if (isLoading && filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            } else if (filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (searchQuery.isBlank()) "No hay pedidos registrados." else "No se encontraron resultados.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        OrderItem(order = order, onClick = { onNavigateToDetail(order.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        placeholder = { Text("Buscar por ID, cliente o estado...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        singleLine = true,
        shape = RoundedCornerShape(AppRadius.xl)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderItem(order: Order, onClick: () -> Unit) {
    val random = Random(order.id) // Semilla para consistencia
    val randomClient = clientList[random.nextInt(clientList.size)]
    val clientName = order.cliente?.nombreCompleto?.takeIf { it.isNotBlank() } ?: randomClient.name

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Pedido #${order.id}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                OrderStatusBadge(status = order.estado)
            }
            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(label = "Cliente:", value = clientName)
            InfoRow(label = "F. Entrega:", value = order.deliveryDate?.take(10) ?: "No especificada")

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total", fontSize = 16.sp, color = Color.Gray)
                Text("$${String.format(Locale.getDefault(), "%,.2f", order.total ?: 0.0)}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF16A34A))
            }
        }
    }
}
