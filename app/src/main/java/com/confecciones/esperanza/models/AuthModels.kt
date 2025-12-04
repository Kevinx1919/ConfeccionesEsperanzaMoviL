package com.confecciones.esperanza.models

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)

data class LoginResponse(
    val isSuccess: Boolean,
    val message: String,
    val token: String? = null,
    val tokenExpiration: String? = null,
    val user: User? = null
)

data class User(
    val id: String,
    val userName: String,
    val email: String,
    val phoneNumber: String?,
    val emailConfirmed: Boolean,
    val phoneNumberConfirmed: Boolean,
    val twoFactorEnabled: Boolean,
    val lockoutEnabled: Boolean,
    val lockoutEnd: String?,
    val accessFailedCount: Int,
    val roles: List<String>
)