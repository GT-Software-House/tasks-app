package org.gabrielsantana.tasks.features.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpauth.google.GoogleUser
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState(isSignedIn = Firebase.auth.currentUser != null))
    val uiState = _uiState

    fun signInWithGoogleUser(googleUser: GoogleUser?) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isError = false, isLoading = true) }
                val credential = GoogleAuthProvider.credential(googleUser?.idToken, googleUser?.accessToken)
                Firebase.auth.signInWithCredential(credential)
                _uiState.update { it.copy(isSignedIn = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isError = true, isLoading = false) }
            }
        }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val isError: Boolean = false
)