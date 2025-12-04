package com.confecciones.esperanza.models

data class ProfileData(
    val id: String? = null,
    val userName: String? = null,
    val email: String? = null,
    val emailConfirmed: Boolean = false,
    val phoneNumber: String? = null,
    val phoneNumberConfirmed: Boolean = false,
    val twoFactorEnabled: Boolean = false,
    val lockoutEnabled: Boolean = false,
    val lockoutEnd: String? = null,
    val accessFailedCount: Int = 0,
    val roles: List<String>? = null
)

data class ProfileUpdateRequest(
    val userName: String,
    val email: String,
    val phoneNumber: String?
)

data class ProfileResponse(
    val isSuccess: Boolean,
    val message: String,
    val data: ProfileData? = null
)