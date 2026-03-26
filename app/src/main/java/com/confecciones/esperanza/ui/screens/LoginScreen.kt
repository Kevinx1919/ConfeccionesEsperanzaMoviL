package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.ui.theme.AppColors
import com.confecciones.esperanza.ui.theme.FullWidthPrimaryAppButton
import com.confecciones.esperanza.ui.theme.AppGradients
import com.confecciones.esperanza.ui.theme.AppRadius
import com.confecciones.esperanza.ui.theme.AppSpacing
import com.confecciones.esperanza.viewmodels.LoginUiState
import com.confecciones.esperanza.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showRecoveryInfo by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

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
            .background(AppGradients.primary)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .align(Alignment.Center),
            shape = RoundedCornerShape(AppRadius.xl),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Confecciones Esperanza",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.textSecondary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = "Inicia sesion",
                    style = MaterialTheme.typography.displaySmall,
                    color = AppColors.primary
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Text(
                    text = "Accede al panel de gestion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    placeholder = { Text("usuario@empresa.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState !is LoginUiState.Loading,
                    shape = RoundedCornerShape(AppRadius.md)
                )

                Spacer(modifier = Modifier.height(AppSpacing.md))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contrasena") },
                    placeholder = { Text("Ingresa tu contrasena") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState !is LoginUiState.Loading,
                    shape = RoundedCornerShape(AppRadius.md),
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(AppSpacing.md))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        enabled = uiState !is LoginUiState.Loading
                    )
                    Text(
                        text = "Recordar sesion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.md))

                FullWidthPrimaryAppButton(
                    text = "Entrar",
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(email, password, rememberMe)
                        }
                    },
                    enabled = uiState !is LoginUiState.Loading && email.isNotBlank() && password.isNotBlank(),
                    content = {
                        if (uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Entrar", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                TextButton(onClick = { showRecoveryInfo = !showRecoveryInfo }) {
                    Text("Olvidaste tu contrasena?")
                }

                if (showRecoveryInfo) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppSpacing.xs),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF2F8)),
                        shape = RoundedCornerShape(AppRadius.md)
                    ) {
                        Text(
                            text = "La recuperacion de contrasena se gestiona con el administrador.",
                            modifier = Modifier.padding(AppSpacing.md),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.rose
                        )
                    }
                }

                if (uiState is LoginUiState.Error) {
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(AppRadius.md)
                    ) {
                        Text(
                            text = (uiState as LoginUiState.Error).message,
                            modifier = Modifier.padding(AppSpacing.md),
                            color = AppColors.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
