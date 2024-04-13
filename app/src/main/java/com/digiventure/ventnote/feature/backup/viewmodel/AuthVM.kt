package com.digiventure.ventnote.feature.backup.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthVM @Inject constructor(
    private val app: Application,
) : ViewModel(), AuthBaseVM {

    private val _uiState = mutableStateOf(AuthPageState())
    override val uiState: State<AuthPageState> = _uiState

    private val _eventFlow = MutableSharedFlow<AuthState>()
    override val eventFlow = _eventFlow.asSharedFlow()

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(app.applicationContext, getGoogleSignInOptions())
    }

    init {
        checkAuthState()
    }

    override fun signOut() {
        googleSignInClient.signOut()
            .addOnCompleteListener {
                checkAuthState()
            }
    }

    override fun getSignInIntent() = googleSignInClient.signInIntent

    override fun checkAuthState() {
        val lastUser = getLastSignedUser()
        val authState = if (lastUser == null) AuthState.SignedOut else AuthState.SignedIn
        _uiState.value = AuthPageState(authState)
        viewModelScope.launch {
            _eventFlow.emit(authState)
        }
    }

    private fun getLastSignedUser(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(app.applicationContext)
    }

    private fun getGoogleSignInOptions(): GoogleSignInOptions {
        val scopeDriveAppFolder = Scope(Scopes.DRIVE_APPFOLDER)
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(scopeDriveAppFolder)
            .build()
    }

    data class AuthPageState(
        var authState: AuthState = AuthState.Loading
    )

    sealed interface AuthState {
        object Loading : AuthState
        object SignedOut : AuthState
        object SignedIn : AuthState
    }
}
