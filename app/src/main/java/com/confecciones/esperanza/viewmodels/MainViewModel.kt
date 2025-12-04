package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun setUserData(name: String, token: String, role: String = "Usuario") {
        _userName.value = name
        _token.value = token
        _userRole.value = role
        _isLoggedIn.value = true
    }

    fun logout() {
        _userName.value = null
        _token.value = null
        _userRole.value = null
        _isLoggedIn.value = false
    }
}