package com.digiventure.ventnote.feature.backup.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
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

    private var googleSignInClient: GoogleSignInClient

    init {
        googleSignInClient =
            GoogleSignIn.getClient(app.applicationContext, getGoogleSignInOptions())
        checkAuthState()
    }

    override fun signOut(onCompleteSignOutCallback: () -> Unit) {
        googleSignInClient.signOut()
            .addOnCompleteListener {
                checkAuthState()
                onCompleteSignOutCallback()
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
        val scopeDriveAppFolder = Scope(DriveScopes.DRIVE_APPDATA)
        val idToken = app.getString(R.string.web_client_id)
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(idToken)
            .requestId()
            .requestScopes(scopeDriveAppFolder)
            .build()
    }

    data class AuthPageState(
        var authState: AuthState = AuthState.Loading
    )

    sealed interface AuthState {
        data object Loading : AuthState
        data object SignedOut : AuthState
        data object SignedIn : AuthState
    }
}
