package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.confecciones.esperanza.viewmodels.OrderViewModel
import java.util.Locale

@Composable
fun OrderListScreen(
    token: String,
    orderViewModel: OrderViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    LaunchedEffect(token) { if (token.isNotBlank()) orderViewModel.loadOrders(token) }

    val filteredOrders by orderViewModel.filteredOrders.collectAsState()
    val searchQuery by orderViewModel.searchQuery.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    val error by orderViewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderItem(order = order, onClick = { onNavigateToDetail(order.id) })
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
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        placeholder = { Text("Buscar por ID, cliente o estado...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        singleLine = true,
        shape = RoundedCornerShape(50)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderItem(order: Order, onClick: () -> Unit) {
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

            val customerName = listOfNotNull(order.cliente?.nombreCliente, order.cliente?.apellidoCliente).joinToString(" ").ifEmpty { "Cliente N/A" }
            InfoRow(label = "Cliente:", value = customerName)
            InfoRow(label = "F. Entrega:", value = order.deliveryDate?.take(10) ?: "No especificada")

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total", fontSize = 16.sp, color = Color.Gray)
                Text("$${String.format(Locale.getDefault(), "%,.2f", order.total ?: 0.0)}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF16A34A))
            }
        }
    }
}
