package com.example.data.repository

import com.example.data.local.entities.UserProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val email: String, val isDemo: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthRepository(private val forjaRepository: ForjaRepository) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun login(email: String, pass: String): Boolean {
        if (email.isBlank() || pass.length < 6) {
            _authState.value = AuthState.Error("Informe um e-mail válido e senha com no mínimo 6 caracteres.")
            return false
        }
        _authState.value = AuthState.Loading
        // In local mode, store/authenticate user profile
        val existing = forjaRepository.getUserProfile()
        _authState.value = AuthState.Authenticated(email, isDemo = false)
        return true
    }

    suspend fun register(name: String, email: String, pass: String): Boolean {
        if (name.isBlank() || email.isBlank() || pass.length < 6) {
            _authState.value = AuthState.Error("Preencha todos os campos corretamente (senha mín. 6 caracteres).")
            return false
        }
        _authState.value = AuthState.Loading
        val newProfile = UserProfileEntity(
            id = 1,
            name = name,
            email = email,
            isRegistered = true,
            isOnboarded = false,
            isDemoUser = false
        )
        forjaRepository.saveUserProfile(newProfile)
        _authState.value = AuthState.Authenticated(email, isDemo = false)
        return true
    }

    suspend fun loginDemo(): Boolean {
        _authState.value = AuthState.Loading
        forjaRepository.populateDemoData()
        _authState.value = AuthState.Authenticated("demo@forjagym.com", isDemo = true)
        return true
    }

    fun recoverPassword(email: String): String {
        return if (email.contains("@")) {
            "Instruções para redefinição enviadas para $email."
        } else {
            "Por favor, insira um e-mail válido."
        }
    }

    suspend fun logout() {
        _authState.value = AuthState.Idle
    }
}
