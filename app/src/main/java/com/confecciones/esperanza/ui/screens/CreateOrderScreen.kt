package com.confecciones.esperanza.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.confecciones.esperanza.models.Cliente
import com.confecciones.esperanza.models.OrderRequest
import com.confecciones.esperanza.models.NewOrderDetail
import com.confecciones.esperanza.viewmodels.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(navController: NavController, token: String, orderViewModel: OrderViewModel = viewModel()) {

    LaunchedEffect(token) { if (token.isNotBlank()) orderViewModel.loadClients(token) }

    val clients by orderViewModel.clients.collectAsState()
    var selectedClient by remember { mutableStateOf<Cliente?>(null) }
    var deliveryDate by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<NewOrderDetail>>(emptyList()) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Nuevo Pedido") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            Row(Modifier.padding(16.dp).fillMaxWidth()) {
                OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                    Text("CANCELAR")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        val order = OrderRequest(clienteId = selectedClient!!.idCliente, fechaEntrega = deliveryDate, detalles = details)
                        orderViewModel.createOrder(token, order) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedClient != null && deliveryDate.isNotBlank() && details.isNotEmpty()
                ) {
                    Text("REGISTRAR")
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
            item { Text("1. Datos Generales", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            item { ClientSelector(clients, selectedClient) { selectedClient = it } }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { DateSelector(deliveryDate) { deliveryDate = it } }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp)) }
            item { Text("2. Productos del Pedido", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            item { AddProductSection { newDetail -> details = details + newDetail } }
            if (details.isNotEmpty()) {
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { Text("Resumen del Pedido", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
                items(details, key = { it.productoId }) { detail ->
                    val productName = productList.find { it.id == detail.productoId }?.name ?: ""
                    AddedProductItem(productName, detail.cantidad) { details = details - detail }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientSelector(clients: List<Cliente>, selectedClient: Cliente?, onClientSelected: (Cliente) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedClient?.nombreCompleto ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Seleccione un cliente") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            clients.forEach { client ->
                DropdownMenuItem(
                    text = { Text(client.nombreCompleto ?: "") },
                    onClick = { onClientSelected(client); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun DateSelector(deliveryDate: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    OutlinedTextField(
        value = deliveryDate,
        onValueChange = {},
        readOnly = true,
        label = { Text("Fecha de Entrega") },
        trailingIcon = {
            IconButton(onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        calendar.set(year, month, day)
                        onDateSelected(dateFormat.format(calendar.time))
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductSection(onAddProduct: (NewOrderDetail) -> Unit) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf("1") }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)).padding(16.dp)) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedProduct?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleccione un producto") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                productList.forEach { product ->
                    DropdownMenuItem(
                        text = { Text(product.name) },
                        onClick = { selectedProduct = product; expanded = false }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = if (selectedProduct != null) String.format(Locale.getDefault(), "%,.0f", selectedProduct?.price) else "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Precio Unitario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it.filter { c -> c.isDigit() } },
            label = { Text("Cantidad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val product = selectedProduct!!
                onAddProduct(NewOrderDetail(product.id, quantity.toInt(), product.price))
                selectedProduct = null // Reset
                quantity = "1"
            },
            enabled = selectedProduct != null && (quantity.toIntOrNull() ?: 0) > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("AÑADIR PRODUCTO")
        }
    }
}

@Composable
private fun AddedProductItem(productName: String, quantity: Int, onRemove: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$quantity x $productName", modifier = Modifier.weight(1f))
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Quitar", tint = MaterialTheme.colorScheme.error)
        }
    }
}
