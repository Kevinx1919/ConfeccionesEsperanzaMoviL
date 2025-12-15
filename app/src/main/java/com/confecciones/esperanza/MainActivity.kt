package com.confecciones.esperanza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.confecciones.esperanza.ui.screens.*
import com.confecciones.esperanza.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
    val userName by mainViewModel.userName.collectAsState()
    val token by mainViewModel.token.collectAsState()

    val startDestination = if (isLoggedIn) "dashboard" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { name, authToken ->
                    mainViewModel.setUserData(name, authToken)
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                userName = userName ?: "Usuario",
                token = token ?: "",
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToClientes = {
                    navController.navigate("clientes")
                },
                onNavigateToStockDetail = { materialId ->
                    navController.navigate("material_detail/$materialId")
                },
                onNavigateToStockCreate = {
                    navController.navigate("stock_create")
                },
                onNavigateToTasks = {
                    navController.navigate("tasks")
                },
                 onNavigateToEmployees = {
                    navController.navigate("employees")
                },
                mainViewModel = mainViewModel
            )
        }

        composable("profile") {
            ProfileScreen(
                token = token ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("clientes") {
            ClientesScreen(
                token = token ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { clienteId ->
                    navController.navigate("cliente_detail/$clienteId")
                },
                onNavigateToCreate = {
                    navController.navigate("cliente_create")
                }
            )
        }

        composable(
            route = "cliente_detail/{clienteId}",
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            ClienteDetailScreen(
                clienteId = clienteId,
                token = token ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    navController.navigate("cliente_edit/$id")
                },
                onNavigateToDelete = { id ->
                    navController.navigate("cliente_delete/$id")
                }
            )
        }

        composable("cliente_create") {
            CreateClienteScreen(
                token = token ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "cliente_edit/{clienteId}",
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            EditClienteScreen(
                clienteId = clienteId,
                token = token ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "cliente_delete/{clienteId}",
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            DeleteClienteScreen(
                clienteId = clienteId,
                token = token ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("stock_create") {
            CreateMaterialScreen(
                token = token ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "material_detail/{materialId}",
            arguments = listOf(navArgument("materialId") { type = NavType.IntType })
        ) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getInt("materialId") ?: 0
            MaterialDetailScreen(
                materialId = materialId,
                token = token ?: "",
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate("material_edit/$id")
                },
                onNavigateToDelete = { id ->
                    navController.navigate("material_delete/$id")
                }
            )
        }

        composable(
            route = "material_edit/{materialId}",
            arguments = listOf(navArgument("materialId") { type = NavType.IntType })
        ) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getInt("materialId") ?: 0
            EditMaterialScreen(
                materialId = materialId,
                token = token ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "material_delete/{materialId}",
            arguments = listOf(navArgument("materialId") { type = NavType.IntType })
        ) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getInt("materialId") ?: 0
            DeleteMaterialScreen(
                materialId = materialId,
                token = token ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("tasks") {
            TasksScreen(
                token = token ?: "",
                onNavigateToDetail = { taskId ->
                    // TODO: Navigate to task detail
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = { /* TODO: Navigate to create task */ }
            )
        }
        composable("employees") {
            EmployeesScreen(
                token = token ?: "",
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { employeeId ->
                    // TODO: Navigate to employee detail
                },
                onNavigateToCreate = { /* TODO: Navigate to create employee */ }
            )
        }
    }
}