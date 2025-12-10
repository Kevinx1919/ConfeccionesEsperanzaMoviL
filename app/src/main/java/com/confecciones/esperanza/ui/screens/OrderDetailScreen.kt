package com.confecciones.esperanza.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavController
import com.confecciones.esperanza.models.Order
import com.confecciones.esperanza.models.OrderDetail
import com.confecciones.esperanza.viewmodels.OrderViewModel
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: Int,
    token: String,
    orderViewModel: OrderViewModel = viewModel()
) {
    LaunchedEffect(orderId, token) {
        if (token.isNotBlank()) {
            orderViewModel.loadOrderById(token, orderId)
        }
    }

    val order by orderViewModel.selectedOrder.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (order != null) "Detalle Pedido #${order?.id}" else "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        if (isLoading || order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
                item { StatusSection(order!!) }
                item { GeneralInfoSection(order!!) }
                item { ProductsSection(order!!) }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun StatusSection(order: Order) {
    DetailSection(title = "Gestión de Estado") {
        val scrollState = rememberScrollState()
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
            val states = listOf("En Proceso", "En Producción", "Completado", "Cancelado")
            states.forEach { state ->
                Button(
                    onClick = { /* TODO: Implementar lógica de cambio de estado */ },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (order.estado == state) MaterialTheme.colorScheme.primary else Color.LightGray,
                        contentColor = if (order.estado == state) Color.White else Color.Black
                    )
                ) {
                    Text(state)
                }
            }
        }
    }
}

@Composable
private fun GeneralInfoSection(order: Order) {
    DetailSection(title = "Información General") {
        val random = Random(order.id)
        val randomClient = clientList[random.nextInt(clientList.size)]

        InfoRow(label = "Cliente", value = randomClient.name)
        InfoRow(label = "F. Registro", value = order.registrationDate?.take(10))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("F. Entrega", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.width(90.dp))
            Text(text = order.deliveryDate?.take(10) ?: "N/A", fontWeight = FontWeight.Normal, fontSize = 14.sp)
            if (order.estaVencido == true) {
                Text(" (Vencido)", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        InfoRow(label = "Últ. Act.", value = order.fechaActualizacion?.take(16)?.replace("T", " ") ?: "N/A")
    }
}

@Composable
private fun ProductsSection(order: Order) {
    DetailSection(title = "Productos Solicitados (${order.totalItems ?: 0})") {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text("Producto", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
            Text("Cant.", modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("P. Unit.", modifier = Modifier.weight(1.2f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
            Text("Subtotal", modifier = Modifier.weight(1.2f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()

        val randomSeed = order.id //
        val random = Random(randomSeed)
        val shuffledProducts = productList.shuffled(random)

        order.details?.forEachIndexed { index, detail ->
            ProductDetailItem(detail, index, shuffledProducts)
        }

        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("TOTAL PEDIDO:", modifier = Modifier.weight(1f), fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(
                text = "$${String.format(Locale.getDefault(), "%,.2f", order.total ?: 0.0)}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ProductDetailItem(detail: OrderDetail, index: Int, shuffledProducts: List<Product>) {
    val product = shuffledProducts[index % shuffledProducts.size]

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.Top) {
        Text(product.name, modifier = Modifier.weight(2f), fontSize = 14.sp)
        Text("${detail.cantidad ?: 0}", modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center, fontSize = 14.sp)
        Text(String.format(Locale.getDefault(), "$%,.0f", detail.unitPrice ?: 0.0), modifier = Modifier.weight(1.2f), textAlign = TextAlign.End, fontSize = 14.sp)
        Text(String.format(Locale.getDefault(), "$%,.0f", detail.subtotal ?: 0.0), modifier = Modifier.weight(1.2f), textAlign = TextAlign.End, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
}
