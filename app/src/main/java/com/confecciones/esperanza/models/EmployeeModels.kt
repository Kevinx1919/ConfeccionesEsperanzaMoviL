package com.confecciones.esperanza.models

import com.google.gson.annotations.SerializedName

data class Employee(
    val id: String,
    val userName: String,
    val email: String,
    val phoneNumber: String?,
    val roles: List<String>?,
    val emailConfirmed: Boolean,
    val twoFactorEnabled: Boolean,
    val lockoutEnabled: Boolean,
    val accessFailedCount: Int
)

data class EmployeesResponse(
    @SerializedName("users")
    val employees: List<Employee>
)
