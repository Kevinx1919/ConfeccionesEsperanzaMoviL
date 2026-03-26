package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.models.MetricasResponse
import com.confecciones.esperanza.models.ProductividadUsuario
import com.confecciones.esperanza.ui.theme.*
import com.confecciones.esperanza.viewmodels.DashboardUiState
import com.confecciones.esperanza.viewmodels.DashboardViewModel
import com.confecciones.esperanza.viewmodels.MainViewModel
import kotlinx.coroutines.launch

enum class TopMenuItem(val title: String) {
    INICIO("Dashboard"),
    PEDIDOS("Pedidos"),
    STOCK("Stock"),
    REPORTES("Reportes")
}

data class DrawerMenuItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String,
    val isDivider: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToClientes: () -> Unit,
    onNavigateToStockCreate: () -> Unit,
    onNavigateToStockDetail: (Int) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToEmployees: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCreateOrder: () -> Unit,
    onNavigateToOrderDetail: (Int) -> Unit,
    mainViewModel: MainViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    var selectedTopItem by remember { mutableStateOf(TopMenuItem.INICIO) }
    var previousTopItem by remember { mutableStateOf(TopMenuItem.INICIO) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun selectTopItem(target: TopMenuItem) {
        if (selectedTopItem != target) {
            previousTopItem = selectedTopItem
            selectedTopItem = target
        }
    }

    val drawerItems = listOf(
        DrawerMenuItem("Mi perfil", Icons.Outlined.Person, "perfil"),
        DrawerMenuItem("", Icons.Outlined.Person, "", isDivider = true),
        DrawerMenuItem("Pedidos", Icons.Outlined.Description, "pedidos"),
        DrawerMenuItem("Empleados", Icons.Outlined.Group, "empleados"),
        DrawerMenuItem("Tareas", Icons.Outlined.FactCheck, "tareas"),
        DrawerMenuItem("Clientes", Icons.Outlined.Person, "clientes"),
        DrawerMenuItem("Stock", Icons.Outlined.Inventory2, "stock")
    )

    LaunchedEffect(Unit) {
        dashboardViewModel.loadMetricas()
        dashboardViewModel.loadProductividad()
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
                        "tareas" -> onNavigateToTasks()
                        "empleados" -> onNavigateToEmployees()
                        "pedidos" -> onNavigateToOrders()
                        "stock" -> selectTopItem(TopMenuItem.STOCK)
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
                    onItemSelected = { selectTopItem(it) },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            floatingActionButton = {
                when (selectedTopItem) {
                    TopMenuItem.STOCK -> {
                        FloatingActionButton(onClick = onNavigateToStockCreate) {
                            Icon(Icons.Outlined.Inventory2, contentDescription = null)
                        }
                    }
                    TopMenuItem.PEDIDOS -> {
                        FloatingActionButton(onClick = onNavigateToCreateOrder) {
                            Icon(Icons.Outlined.Description, contentDescription = null)
                        }
                    }
                    else -> Unit
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(AppBackground)
            ) {
                when (selectedTopItem) {
                    TopMenuItem.INICIO -> InicioContent(
                        userName = userName,
                        dashboardViewModel = dashboardViewModel
                    )
                    TopMenuItem.PEDIDOS -> OrderListScreen(
                        onNavigateToDetail = onNavigateToOrderDetail
                    )
                    TopMenuItem.STOCK -> StockScreen(
                        onNavigateToDetail = onNavigateToStockDetail
                    )
                    TopMenuItem.REPORTES -> ReportesScreen(
                        onOpenStock = { selectTopItem(TopMenuItem.STOCK) },
                        onOpenPedidos = { selectTopItem(TopMenuItem.PEDIDOS) },
                        onOpenEmpleados = onNavigateToEmployees,
                        onOpenTareas = onNavigateToTasks,
                        onOpenClientes = onNavigateToClientes
                    )
                }

                if (selectedTopItem != TopMenuItem.INICIO) {
                    ExtendedFloatingActionButton(
                        onClick = { selectedTopItem = previousTopItem },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = AppSpacing.md, bottom = AppSpacing.md),
                        icon = {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        },
                        text = {
                            Text("Volver")
                        },
                        containerColor = AppSurface,
                        contentColor = AppTextPrimary
                    )
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(AppGradients.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "U",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary
                    )
                }
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Text(text = userName, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(text = "Usuario", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))

        drawerItems.forEach { item ->
            if (item.isDivider) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm))
            } else {
                NavigationDrawerItem(
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(text = item.title, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { onItemClick(item.route) },
                    modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(modifier = Modifier.padding(horizontal = AppSpacing.md))
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = AppError) },
            label = { Text(text = "Cerrar sesion", color = AppError, fontWeight = FontWeight.Medium) },
            selected = false,
            onClick = onLogout,
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))
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
            .background(AppGradients.header)
    ) {
        TopAppBar(
            title = { Text(text = selectedItem.title, fontWeight = FontWeight.Bold, color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(imageVector = Icons.Outlined.Menu, contentDescription = null, tint = Color.White)
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
    dashboardViewModel: DashboardViewModel
) {
    val metricasState by dashboardViewModel.metricasState.collectAsState()
    val productividadState by dashboardViewModel.productividadState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.md)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = userName.firstOrNull()?.uppercase() ?: "U", fontWeight = FontWeight.Bold, color = PurplePrimary)
                }
                Spacer(modifier = Modifier.width(AppSpacing.md))
                Column {
                    Text(text = "Hola, $userName", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Resumen operativo del dia", style = MaterialTheme.typography.bodyMedium, color = AppTextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))

        when (val state = metricasState) {
            is DashboardUiState.Loading -> {
                LoadingBlock("Cargando metricas...")
            }
            is DashboardUiState.Success<*> -> {
                val data = state.data as MetricasResponse
                MetricsRow(data)
                Spacer(modifier = Modifier.height(AppSpacing.md))
                NextOrderCard(data)
            }
            is DashboardUiState.Error -> {
                ErrorCard(state.message)
            }
            else -> Unit
        }

        when (val state = productividadState) {
            is DashboardUiState.Loading -> {
                Spacer(modifier = Modifier.height(AppSpacing.lg))
                SectionHeader(title = "Productividad", icon = Icons.Outlined.BarChart)
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                LoadingBlock("Cargando productividad...")
            }
            is DashboardUiState.Success<*> -> {
                val items = (state.data as List<ProductividadUsuario>).filter { it.hasUsefulContent() }
                if (items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(AppSpacing.lg))
                    SectionHeader(title = "Productividad", icon = Icons.Outlined.BarChart)
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    items.take(4).forEach { item ->
                        ProductivityRow(item)
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                    }
                }
            }
            is DashboardUiState.Error -> Unit
            else -> Unit
        }

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

private fun ProductividadUsuario.hasUsefulContent(): Boolean {
    return !userName.isNullOrBlank() ||
        (tareasCompletadas ?: 0) > 0 ||
        (tareasEnProceso ?: 0) > 0 ||
        (eficiencia ?: 0.0) > 0.0
}

@Composable
private fun MetricsRow(data: MetricasResponse) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        MetricCard(
            title = "Pedidos pendientes",
            value = data.pedidosPendientes.size.toString(),
            highlight = PurplePrimary
        )
        MetricCard(
            title = "Proximo pedido",
            value = "#${data.proximoPedido.idPedido}",
            highlight = BlueAccent
        )
    }
}

@Composable
private fun RowScope.MetricCard(title: String, value: String, highlight: Color) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = AppTextSecondary)
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(text = value, style = MaterialTheme.typography.titleLarge, color = highlight)
        }
    }
}

@Composable
private fun NextOrderCard(data: MetricasResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text("Proximo pedido", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                text = "${data.proximoPedido.clienteNombre} - #${data.proximoPedido.idPedido}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTextSecondary
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            LinearProgressIndicator(
                progress = { (data.proximoPedido.porcentajeCompletado / 100).toFloat() },
                color = PurplePrimary,
                trackColor = AppOutline
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = "Entrega ${data.proximoPedido.fechaEntrega.take(10)}",
                style = MaterialTheme.typography.labelMedium,
                color = AppTextMuted
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PurplePrimary)
        Spacer(modifier = Modifier.width(AppSpacing.sm))
        Text(text = title, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ProductivityRow(item: ProductividadUsuario) {
    val userName = item.userName?.takeIf { it.isNotBlank() } ?: "Usuario"
    val tareasCompletadas = item.tareasCompletadas ?: 0
    val tareasEnProceso = item.tareasEnProceso ?: 0
    val eficiencia = ((item.eficiencia ?: 0.0) / 100).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(text = userName, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Completadas: $tareasCompletadas", style = MaterialTheme.typography.labelMedium, color = AppTextSecondary)
                Text(text = "En proceso: $tareasEnProceso", style = MaterialTheme.typography.labelMedium, color = AppTextSecondary)
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            LinearProgressIndicator(
                progress = { eficiencia },
                color = BlueAccent,
                trackColor = AppOutline
            )
        }
    }
}

@Composable
private fun LoadingBlock(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PurplePrimary)
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(text, style = MaterialTheme.typography.bodySmall, color = AppTextSecondary)
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceAlt)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(AppSpacing.md),
            style = MaterialTheme.typography.bodyMedium,
            color = AppTextSecondary
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
    ) {
        Row(modifier = Modifier.padding(AppSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = AppError)
            Spacer(modifier = Modifier.width(AppSpacing.sm))
            Text(text = message, style = MaterialTheme.typography.bodySmall, color = AppError)
        }
    }
}

@Composable
fun ReportesScreen(
    onOpenStock: () -> Unit,
    onOpenPedidos: () -> Unit,
    onOpenEmpleados: () -> Unit,
    onOpenTareas: () -> Unit,
    onOpenClientes: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.md)
    ) {
        Text(text = "Centro de reportes", style = MaterialTheme.typography.titleLarge)
        Text(text = "Analisis rapido por modulo", style = MaterialTheme.typography.bodyMedium, color = AppTextSecondary)

        Spacer(modifier = Modifier.height(AppSpacing.md))

        val reportes = listOf(
            ReportModule("Stock", "Insumos con menor cobertura y rotacion semanal", "3 materiales requieren reposicion", onOpenStock),
            ReportModule("Pedidos", "Seguimiento de entregas y carga activa", "2 pedidos con entrega cercana", onOpenPedidos),
            ReportModule("Empleados", "Estado general del equipo", "4 usuarios sostienen la mayor productividad", onOpenEmpleados),
            ReportModule("Tareas", "Avance operativo del modulo actual", "La mayoria sigue en flujo normal", onOpenTareas),
            ReportModule("Clientes", "Movimiento comercial reciente", "1 cliente concentra mas actividad del dia", onOpenClientes)
        )

        reportes.forEach { modulo ->
            ReportCard(
                modulo = modulo.title,
                resumen = modulo.summary,
                detalle = modulo.detail,
                onClick = modulo.onOpen
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        Text(text = "Analisis rapido", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        AnalysisCard()

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

@Composable
private fun ReportCard(
    modulo: String,
    resumen: String,
    detalle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(AppRadius.sm))
                        .background(PurplePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.BarChart, contentDescription = null, tint = PurplePrimary)
                }
                Spacer(modifier = Modifier.width(AppSpacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = modulo, fontWeight = FontWeight.SemiBold)
                    Text(text = resumen, style = MaterialTheme.typography.bodySmall, color = AppTextSecondary)
                }
                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = AppTextMuted)
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Surface(
                color = AppSurfaceAlt,
                shape = RoundedCornerShape(AppRadius.md)
            ) {
                Text(
                    text = detalle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.sm),
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTextPrimary
                )
            }
        }
    }
}

@Composable
private fun AnalysisCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceAlt),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(text = "Indicadores clave", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                AnalysisMetric(title = "Cumplimiento", value = "86%")
                AnalysisMetric(title = "Retrasos", value = "12")
                AnalysisMetric(title = "Productividad", value = "7.8")
            }
            Spacer(modifier = Modifier.height(AppSpacing.md))
            LinearProgressIndicator(
                progress = { 0.72f },
                color = PinkAccent,
                trackColor = AppOutline
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = "Evolucion semanal", style = MaterialTheme.typography.bodySmall, color = AppTextSecondary
            )
        }
    }
}

@Composable
private fun RowScope.AnalysisMetric(title: String, value: String) {
    Column(modifier = Modifier.weight(1f)) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = AppTextSecondary)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
    }
}

private data class ReportModule(
    val title: String,
    val summary: String,
    val detail: String,
    val onOpen: () -> Unit
)
