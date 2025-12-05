package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
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
import com.confecciones.esperanza.viewmodels.DashboardViewModel
import com.confecciones.esperanza.viewmodels.MainViewModel
import kotlinx.coroutines.launch

enum class TopMenuItem(val title: String) {
    INICIO("Inicio"),
    PEDIDOS("Pedidos"),
    STOCK("Stock"),
    REPORTES("Reportes")
}

data class DrawerMenuItem(
    val title: String,
    val icon: String,
    val route: String,
    val isDivider: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String,
    token: String,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToClientes: () -> Unit,
    onNavigateToStockCreate: () -> Unit,
    onNavigateToStockDetail: (Int) -> Unit,
    mainViewModel: MainViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    var selectedTopItem by remember { mutableStateOf(TopMenuItem.STOCK) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        DrawerMenuItem("Mi Perfil", "👤", "perfil"),
        DrawerMenuItem("", "", "", isDivider = true),
        DrawerMenuItem("Admin", "👤", "admin"),
        DrawerMenuItem("Empleados", "👥", "empleados"),
        DrawerMenuItem("Tareas", "📋", "tareas"),
        DrawerMenuItem("Clientes", "👨‍👩‍👧‍👦", "clientes"),
        DrawerMenuItem("Configuración", "🔧", "configuracion")
    )

    // Cargar datos al iniciar
    LaunchedEffect(token) {
        dashboardViewModel.loadMetricas(token)
        dashboardViewModel.loadResumenVentas(token)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userName = userName,
                drawerItems = drawerItems,
                onItemClick = { route ->
                    scope.launch { drawerState.close() }
                    when (route) {
                        "perfil" -> onNavigateToProfile()
                        "clientes" -> onNavigateToClientes()
                        // Aquí puedes agregar más rutas cuando las implementes
                    }
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    mainViewModel.logout()
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                DashboardTopBar(
                    selectedItem = selectedTopItem,
                    onItemSelected = { selectedTopItem = it },
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            floatingActionButton = {
                if (selectedTopItem == TopMenuItem.STOCK) {
                    FloatingActionButton(
                        onClick = onNavigateToStockCreate,
                        containerColor = Color(0xFF7C3AED)
                    ) {
                        Icon(Icons.Default.Add, "Agregar Material", tint = Color.White)
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8F8F8))
            ) {
                when (selectedTopItem) {
                    TopMenuItem.INICIO -> InicioContent(userName, dashboardViewModel, token)
                    TopMenuItem.PEDIDOS -> PedidosContent()
                    TopMenuItem.STOCK -> StockScreen(
                        token = token,
                        onNavigateToDetail = onNavigateToStockDetail
                    )
                    TopMenuItem.REPORTES -> ReportesContent()
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    userName: String,
    drawerItems: List<DrawerMenuItem>,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp),
        drawerContainerColor = Color.White
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "U",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "Usuario", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Items
        drawerItems.forEach { item ->
            if (item.isDivider) {
                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            } else {
                NavigationDrawerItem(
                    icon = { Text(text = item.icon, fontSize = 24.sp) },
                    label = { Text(text = item.title, fontSize = 16.sp, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { onItemClick(item.route) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        NavigationDrawerItem(
            icon = { Text(text = "🚪", fontSize = 24.sp) },
            label = { Text(text = "Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFFDC2626)) },
            selected = false,
            onClick = onLogout,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    selectedItem: TopMenuItem,
    onItemSelected: (TopMenuItem) -> Unit,
    onMenuClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF7C3AED), Color(0xFFEC4899))
                )
            )
    ) {
        TopAppBar(
            title = { Text(text = selectedItem.title, fontWeight = FontWeight.Bold, color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        ScrollableTabRow(
            selectedTabIndex = selectedItem.ordinal,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            edgePadding = 0.dp,
            divider = {}
        ) {
            TopMenuItem.values().forEach { item ->
                Tab(
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) },
                    text = {
                        Text(
                            text = item.title,
                            fontWeight = if (selectedItem == item) FontWeight.Bold else FontWeight.Normal,
                            color = Color.White.copy(alpha = if (selectedItem == item) 1f else 0.7f)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun InicioContent(
    userName: String,
    dashboardViewModel: DashboardViewModel,
    token: String
) {
    val metricasState by dashboardViewModel.metricasState.collectAsState()
    val ventasState by dashboardViewModel.ventasState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7C3AED).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👋", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Bienvenido", fontSize = 14.sp, color = Color.Gray)
                    Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Métricas Rápidas
        when (val state = metricasState) {
            is com.confecciones.esperanza.viewmodels.DashboardUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF7C3AED))
                }
            }

            is com.confecciones.esperanza.viewmodels.DashboardUiState.Success<*> -> {
                val data = state.data as com.confecciones.esperanza.models.MetricasResponse
                MetricasCards(data.proximoPedido)
                ProximoPedidoCard(data.proximoPedido, data.progresoProximoPedido)
            }

            is com.confecciones.esperanza.viewmodels.DashboardUiState.Error -> {
                ErrorCard(state.message)
            }

            else -> {}
        }

        // Resumen de Ventas
        when (val state = ventasState) {
            is com.confecciones.esperanza.viewmodels.DashboardUiState.Loading -> {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF7C3AED))
                }
            }

            is com.confecciones.esperanza.viewmodels.DashboardUiState.Success<*> -> {
                val ventas = state.data as com.confecciones.esperanza.models.ResumenVentasResponse
                ResumenVentasCard(ventas)
            }

            is com.confecciones.esperanza.viewmodels.DashboardUiState.Error -> {
                Spacer(modifier = Modifier.height(16.dp))
                ErrorCard(state.message)
            }

            else -> {}
        }
    }
}

@Composable
fun MetricasCards(proximoPedido: com.confecciones.esperanza.models.ProximoPedido) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricaCard(
            modifier = Modifier.weight(1f),
            emoji = "📦",
            label = "Próximo Pedido",
            value = proximoPedido.idPedido.toString()
        )
        MetricaCard(
            modifier = Modifier.weight(1f),
            emoji = "⏳",
            label = "Completado",
            value = "${proximoPedido.porcentajeCompletado.toInt()}%"
        )
    }
}

@Composable
fun MetricaCard(modifier: Modifier, emoji: String, label: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProximoPedidoCard(
    proximoPedido: com.confecciones.esperanza.models.ProximoPedido,
    progreso: com.confecciones.esperanza.models.ProgresoProximoPedido
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Próximo Pedido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Cliente: ${proximoPedido.clienteNombre}", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Pedido #${proximoPedido.idPedido}", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Progreso: ${progreso.tareasCompletadas}/${progreso.totalTareas} tareas", fontSize = 13.sp)
                }
                Text(text = "📋", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = (proximoPedido.porcentajeCompletado / 100).toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF7C3AED)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Entrega: ${proximoPedido.fechaEntrega.take(10)} • ${progreso.diasRestantes} días restantes",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ResumenVentasCard(ventas: com.confecciones.esperanza.models.ResumenVentasResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Resumen de Ventas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VentasColumn(label = "Hoy", monto = ventas.ventasHoy, modifier = Modifier.weight(1f))
                VentasColumn(label = "Esta Semana", monto = ventas.ventasSemana, modifier = Modifier.weight(1f))
                VentasColumn(label = "Este Mes", monto = ventas.ventasMes, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun VentasColumn(label: String, monto: Double, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "$${"%.0f".format(monto)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
    }
}

@Composable
fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "⚠️", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = message, fontSize = 13.sp, color = Color(0xFFC62828))
        }
    }
}

@Composable
fun PedidosContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📦", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Módulo de Pedidos", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ReportesContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📈", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Módulo de Reportes", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}
