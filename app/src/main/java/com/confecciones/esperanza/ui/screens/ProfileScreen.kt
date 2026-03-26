package com.confecciones.esperanza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.confecciones.esperanza.ui.theme.*
import com.confecciones.esperanza.viewmodels.ProfileUiState
import com.confecciones.esperanza.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    var isEditing by remember { mutableStateOf(false) }
    var editUserName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPhoneNumber by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val profileData by viewModel.profileData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    LaunchedEffect(profileData) {
        profileData?.let {
            editUserName = it.userName ?: ""
            editEmail = it.email ?: ""
            editPhoneNumber = it.phoneNumber ?: ""
        }
    }

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
                title = { Text(text = "Mi perfil", fontWeight = FontWeight.SemiBold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBackground)
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PurplePrimary)
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                            Text("Cargando perfil...", color = AppTextSecondary)
                        }
                    }
                }

                is ProfileUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(AppSpacing.lg)) {
                            Text(text = state.message, color = AppError, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                            Button(onClick = { viewModel.fetchProfile() }) {
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

                else -> Unit
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(AppGradients.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.userName?.firstOrNull()?.uppercase() ?: "U",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary
                    )
                }
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Text(text = "Mi perfil", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "Gestion de informacion personal", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
            }
        }

        if (showSuccessMessage) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.md),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                shape = RoundedCornerShape(AppRadius.md)
            ) {
                Row(modifier = Modifier.padding(AppSpacing.md), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = successMessage, color = Color(0xFF15803D), fontSize = 14.sp)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.sm),
            shape = RoundedCornerShape(AppRadius.lg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Informacion personal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary
                    )

                    if (!isEditing) {
                        Button(
                            onClick = onStartEdit,
                            shape = RoundedCornerShape(AppRadius.sm),
                            contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.xs)
                        ) {
                            Text("Editar perfil", fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                if (isEditing) {
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
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppRadius.md)
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppRadius.md)
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Telefono (opcional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppRadius.md)
        )

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(AppRadius.md),
                colors = ButtonDefaults.buttonColors(containerColor = AppSuccess)
            ) {
                Text("Guardar")
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(AppRadius.md)
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun ProfileInfoView(profile: ProfileData) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        ProfileInfoItem(label = "ID de usuario", value = profile.id ?: "N/A")
        ProfileInfoItem(label = "Nombre de usuario", value = profile.userName ?: "N/A")
        ProfileInfoItemWithBadge(
            label = "Correo",
            value = profile.email ?: "N/A",
            badgeText = if (profile.emailConfirmed) "Confirmado" else "Sin confirmar",
            badgeColor = if (profile.emailConfirmed) AppSuccess else AppWarning
        )
        ProfileInfoItemWithBadge(
            label = "Telefono",
            value = profile.phoneNumber ?: "No especificado",
            badgeText = if (profile.phoneNumberConfirmed) "Confirmado" else "Sin confirmar",
            badgeColor = if (profile.phoneNumberConfirmed) AppSuccess else AppWarning
        )
        ProfileInfoItemWithBadge(
            label = "Autenticacion 2FA",
            value = if (profile.twoFactorEnabled) "Habilitada" else "Deshabilitada",
            badgeText = if (profile.twoFactorEnabled) "Activo" else "Inactivo",
            badgeColor = if (profile.twoFactorEnabled) AppSuccess else AppTextMuted
        )
        ProfileInfoItemWithBadge(
            label = "Estado de la cuenta",
            value = if (profile.lockoutEnabled && profile.lockoutEnd != null) "Bloqueada" else "Normal",
            badgeText = if (profile.lockoutEnabled && profile.lockoutEnd != null) "Bloqueada" else "Activa",
            badgeColor = if (profile.lockoutEnabled && profile.lockoutEnd != null) AppError else AppSuccess
        )
        ProfileInfoItem(label = "Intentos fallidos", value = profile.accessFailedCount.toString())

        Column {
            Text(text = "Roles", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppTextMuted)
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            if (profile.roles.isNullOrEmpty()) {
                Text(text = "Sin roles asignados", fontSize = 16.sp, color = AppTextPrimary)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm), modifier = Modifier.fillMaxWidth()) {
                    profile.roles.forEach { role ->
                        Surface(
                            color = PurplePrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(AppRadius.sm)
                        ) {
                            Text(
                                text = role,
                                modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = PurplePrimary
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
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppTextMuted)
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(text = value, fontSize = 16.sp, color = AppTextPrimary)
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
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppTextMuted)
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(text = value, fontSize = 16.sp, color = AppTextPrimary)
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Surface(color = badgeColor.copy(alpha = 0.1f), shape = RoundedCornerShape(AppRadius.sm)) {
            Text(
                text = badgeText,
                modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = badgeColor
            )
        }
    }
}
