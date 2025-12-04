package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.viewmodels.LoginViewModel
import com.confecciones.esperanza.viewmodels.LoginUiState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (String, String) -> Unit // name, token
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    // Manejar estado de éxito
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val response = (uiState as LoginUiState.Success).response
            val userName = response.user?.userName ?: "Usuario"
            val token = response.token ?: ""
            onLoginSuccess(userName, token)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7C3AED), // Purple
                        Color(0xFFEC4899)  // Pink
                    )
                )
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Ingresa tus credenciales para acceder",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Campo de Email
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Email",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7C3AED)
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("usuario@ejemplo.com", color = Color.Gray.copy(alpha = 0.6f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = uiState !is LoginUiState.Loading,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color(0xFFF5F3FF),
                            unfocusedContainerColor = Color(0xFFF8F8F8)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campo de Contraseña
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "🔒",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Contraseña",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7C3AED)
                        )
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Ingresa tu contraseña", color = Color.Gray.copy(alpha = 0.6f)) },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = uiState !is LoginUiState.Loading,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color(0xFFF5F3FF),
                            unfocusedContainerColor = Color(0xFFF8F8F8)
                        ),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "👁️" else "👁️‍🗨️",
                                    fontSize = 20.sp
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox Recordarme
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📋",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        enabled = uiState !is LoginUiState.Loading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF7C3AED)
                        )
                    )
                    Text(
                        "Recordarme",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de Login
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(email, password, rememberMe)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = uiState !is LoginUiState.Loading &&
                            email.isNotBlank() && password.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED),
                        disabledContainerColor = Color(0xFF7C3AED).copy(alpha = 0.5f)
                    )
                ) {
                    if (uiState is LoginUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            "Iniciar Sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Enlaces
                Text(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    fontSize = 13.sp,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontSize = 13.sp,
                    color = Color(0xFF7C3AED)
                )

                // Mostrar mensajes de error
                if (uiState is LoginUiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = (uiState as LoginUiState.Error).message,
                            modifier = Modifier.padding(12.dp),
                            color = Color(0xFFC62828),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}