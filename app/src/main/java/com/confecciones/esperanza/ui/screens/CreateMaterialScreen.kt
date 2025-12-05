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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.models.Color as ColorModel
import com.confecciones.esperanza.models.TipoMaterial
import com.confecciones.esperanza.viewmodels.CreateMaterialViewModel
import com.confecciones.esperanza.viewmodels.CreateMaterialUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMaterialScreen(
    token: String,
    onNavigateBack: () -> Unit,
    viewModel: CreateMaterialViewModel = viewModel()
) {
    val nombre by viewModel.nombre.collectAsState()
    val cantidad by viewModel.cantidad.collectAsState()
    val proveedor by viewModel.proveedor.collectAsState()
    val selectedTipoMaterial by viewModel.selectedTipoMaterial.collectAsState()
    val selectedColor by viewModel.selectedColor.collectAsState()
    val tiposMaterial by viewModel.tiposMaterial.collectAsState()
    val colores by viewModel.colores.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val formError by viewModel.formError.collectAsState()

    LaunchedEffect(token) {
        viewModel.loadDropdowns(token)
    }

    LaunchedEffect(uiState) {
        if (uiState is CreateMaterialUiState.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Material", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7C3AED))
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F8F8))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Información del Material", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = viewModel::onNombreChange,
                            label = { Text("Nombre *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = proveedor,
                            onValueChange = viewModel::onProveedorChange,
                            label = { Text("Proveedor *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Cantidad Counter
                        QuantityCounter(cantidad = cantidad, onCantidadChange = viewModel::onCantidadChange)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Tipo de Material Dropdown
                        MaterialDropdown(tiposMaterial, selectedTipoMaterial, viewModel::onTipoMaterialSelected, "Tipo de Material *")
                        Spacer(modifier = Modifier.height(16.dp))

                        // Color Dropdown
                        ColorDropdown(colores, selectedColor, viewModel::onColorSelected, "Color *")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (formError != null) {
                    Text(formError!!, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
                }

                // Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = onNavigateBack, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { viewModel.createMaterial(token) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        enabled = uiState !is CreateMaterialUiState.Loading
                    ) {
                        Text("Registrar Material")
                    }
                }
            }

            if (uiState is CreateMaterialUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            (uiState as? CreateMaterialUiState.Error)?.let {
                Snackbar(message = it.message, modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}

@Composable
fun QuantityCounter(cantidad: String, onCantidadChange: (String) -> Unit) {
    var cantidadInt = cantidad.toIntOrNull() ?: 1

    OutlinedTextField(
        value = cantidad,
        onValueChange = onCantidadChange,
        label = { Text("Cantidad") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            IconButton(onClick = { onCantidadChange((--cantidadInt).coerceAtLeast(1).toString()) }) {
                Text("-", fontSize = 20.sp)
            }
        },
        trailingIcon = {
            IconButton(onClick = { onCantidadChange((++cantidadInt).toString()) }) {
                Text("+", fontSize = 20.sp)
            }
        },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDropdown(
    items: List<TipoMaterial>,
    selected: TipoMaterial?,
    onSelected: (TipoMaterial) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.descripcion ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.descripcion) },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorDropdown(
    items: List<ColorModel>,
    selected: ColorModel?,
    onSelected: (ColorModel) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.nombre ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.nombre) },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun Snackbar(message: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(16.dp),
        color = Color(0xFF323232),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(text = message, color = Color.White, modifier = Modifier.padding(16.dp))
    }
}
