package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.confecciones.esperanza.models.ProfileData
import com.confecciones.esperanza.viewmodels.ProfileViewModel
import com.confecciones.esperanza.viewmodels.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    token: String,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    var isEditing by remember { mutableStateOf(false) }
    var editUserName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPhoneNumber by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val profileData by viewModel.profileData.collectAsState()

    // Cargar perfil al iniciar
    LaunchedEffect(Unit) {
        viewModel.fetchProfile(token)
    }

    // Actualizar campos de edición cuando cambie profileData
    LaunchedEffect(profileData) {
        profileData?.let {
            editUserName = it.userName ?: ""
            editEmail = it.email ?: ""
            editPhoneNumber = it.phoneNumber ?: ""
        }
    }

    // Manejar éxito de actualización
    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.UpdateSuccess) {
            isEditing = false
            kotlinx.coroutines.delay(2000)
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF7C3AED)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF7C3AED))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando perfil...", color = Color.Gray)
                        }
                    }
                }

                is ProfileUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(text = "⚠️", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = state.message,
                                color = Color(0xFFDC2626),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.fetchProfile(token) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7C3AED)
                                )
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                is ProfileUiState.Success, is ProfileUiState.UpdateSuccess -> {
                    profileData?.let { profile ->
                        ProfileContent(
                            profile = profile,
                            isEditing = isEditing,
                            editUserName = editUserName,
                            editEmail = editEmail,
                            editPhoneNumber = editPhoneNumber,
                            onEditUserName = { editUserName = it },
                            onEditEmail = { editEmail = it },
                            onEditPhoneNumber = { editPhoneNumber = it },
                            onStartEdit = { isEditing = true },
                            onCancelEdit = {
                                isEditing = false
                                editUserName = profile.userName ?: ""
                                editEmail = profile.email ?: ""
                                editPhoneNumber = profile.phoneNumber ?: ""
                            },
                            onSaveEdit = {
                                viewModel.updateProfile(
                                    token,
                                    editUserName,
                                    editEmail,
                                    editPhoneNumber.ifBlank { null }
                                )
                            },
                            showSuccessMessage = state is ProfileUiState.UpdateSuccess,
                            successMessage = if (state is ProfileUiState.UpdateSuccess) state.message else ""
                        )
                    }
                }

                else -> {
                    // Estado Idle - no mostrar nada o mostrar placeholder
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    profile: ProfileData,
    isEditing: Boolean,
    editUserName: String,
    editEmail: String,
    editPhoneNumber: String,
    onEditUserName: (String) -> Unit,
    onEditEmail: (String) -> Unit,
    onEditPhoneNumber: (String) -> Unit,
    onStartEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onSaveEdit: () -> Unit,
    showSuccessMessage: Boolean,
    successMessage: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header con avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7C3AED),
                            Color(0xFFEC4899)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.userName?.firstOrNull()?.uppercase() ?: "U",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Mi Perfil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Administra tu información personal",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Mensaje de éxito
        if (showSuccessMessage) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFDCFCE7)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✅", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = successMessage,
                        color = Color(0xFF15803D),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Contenido principal
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Información Personal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    if (!isEditing) {
                        Button(
                            onClick = onStartEdit,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7C3AED)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Editar Perfil", fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isEditing) {
                    // Formulario de edición
                    EditProfileForm(
                        userName = editUserName,
                        email = editEmail,
                        phoneNumber = editPhoneNumber,
                        onUserNameChange = onEditUserName,
                        onEmailChange = onEditEmail,
                        onPhoneNumberChange = onEditPhoneNumber,
                        onSave = onSaveEdit,
                        onCancel = onCancelEdit
                    )
                } else {
                    // Vista de información
                    ProfileInfoView(profile)
                }
            }
        }
    }
}

@Composable
fun EditProfileForm(
    userName: String,
    email: String,
    phoneNumber: String,
    onUserNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = userName,
            onValueChange = onUserNameChange,
            label = { Text("Nombre de Usuario") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7C3AED),
                focusedLabelColor = Color(0xFF7C3AED)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7C3AED),
                focusedLabelColor = Color(0xFF7C3AED)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Teléfono (Opcional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7C3AED),
                focusedLabelColor = Color(0xFF7C3AED)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Cambios")
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6B7280)
                )
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun ProfileInfoView(profile: ProfileData) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        ProfileInfoItem(
            label = "ID DE USUARIO",
            value = profile.id ?: "N/A"
        )

        ProfileInfoItem(
            label = "NOMBRE DE USUARIO",
            value = profile.userName ?: "N/A"
        )

        ProfileInfoItemWithBadge(
            label = "EMAIL",
            value = profile.email ?: "N/A",
            badgeText = if (profile.emailConfirmed) "✓ CONFIRMADO" else "⚠ SIN CONFIRMAR",
            badgeColor = if (profile.emailConfirmed) Color(0xFF10B981) else Color(0xFFF59E0B)
        )

        ProfileInfoItemWithBadge(
            label = "TELÉFONO",
            value = profile.phoneNumber ?: "No especificado",
            badgeText = if (profile.phoneNumberConfirmed) "✓ CONFIRMADO" else "⚠ SIN CONFIRMAR",
            badgeColor = if (profile.phoneNumberConfirmed) Color(0xFF10B981) else Color(0xFFF59E0B)
        )

        ProfileInfoItemWithBadge(
            label = "AUTENTICACIÓN 2FA",
            value = if (profile.twoFactorEnabled) "Habilitada" else "Deshabilitada",
            badgeText = if (profile.twoFactorEnabled) "✓ ACTIVO" else "INACTIVO",
            badgeColor = if (profile.twoFactorEnabled) Color(0xFF10B981) else Color(0xFF6B7280)
        )

        ProfileInfoItemWithBadge(
            label = "ESTADO DE LA CUENTA",
            value = if (profile.lockoutEnabled && profile.lockoutEnd != null) "Bloqueada" else "Normal",
            badgeText = if (profile.lockoutEnabled && profile.lockoutEnd != null) "🔒 BLOQUEADA" else "✓ ACTIVA",
            badgeColor = if (profile.lockoutEnabled && profile.lockoutEnd != null) Color(0xFFDC2626) else Color(0xFF10B981)
        )

        ProfileInfoItem(
            label = "INTENTOS FALLIDOS",
            value = profile.accessFailedCount.toString()
        )

        // Roles
        Column {
            Text(
                text = "ROLES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (profile.roles.isNullOrEmpty()) {
                Text(
                    text = "Sin roles asignados",
                    fontSize = 16.sp,
                    color = Color(0xFF1F2937)
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    profile.roles.forEach { role ->
                        Surface(
                            color = Color(0xFF7C3AED).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = role,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF7C3AED)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color(0xFF1F2937)
        )
    }
}

@Composable
fun ProfileInfoItemWithBadge(
    label: String,
    value: String,
    badgeText: String,
    badgeColor: Color
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color(0xFF1F2937)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = badgeColor.copy(alpha = 0.1f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = badgeText,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = badgeColor
            )
        }
    }
}