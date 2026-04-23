// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/repository/AuthRepository.kt
// Purpose: Manages Firebase Authentication operations
// ============================================================

package com.app.notisync_receiver.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    companion object {
        private const val TAG = "AuthRepository"
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(currentUser)
            Log.d(TAG, "Existing session found: ${currentUser.email}")
        } else {
            _authState.value = AuthState.Unauthenticated
            Log.d(TAG, "No existing session")
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            _authState.value = AuthState.Loading

            val authResult = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val user = authResult.user

            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                Log.d(TAG, "Login successful: ${user.email}")
                Result.success(user)
            } else {
                val error = Exception("Login succeeded but user is null")
                _authState.value = AuthState.Error(error.message ?: "Unknown error")
                Result.failure(error)
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Login failed")
            Log.e(TAG, "Login failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            _authState.value = AuthState.Loading

            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val user = authResult.user

            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                Log.d(TAG, "Registration successful: ${user.email}")
                Result.success(user)
            } else {
                val error = Exception("Registration succeeded but user is null")
                _authState.value = AuthState.Error(error.message ?: "Unknown error")
                Result.failure(error)
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Registration failed")
            Log.e(TAG, "Registration failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun logout() {
        Log.d(TAG, "Logging out user: ${firebaseAuth.currentUser?.email}")
        firebaseAuth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun clearError() {
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}