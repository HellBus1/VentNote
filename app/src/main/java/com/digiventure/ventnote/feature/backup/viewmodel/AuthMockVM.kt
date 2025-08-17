package com.digiventure.ventnote.feature.backup.viewmodel

import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AuthMockVM: ViewModel(), AuthBaseVM {
    private val _uiState = mutableStateOf(AuthVM.AuthPageState(AuthVM.AuthState.SignedIn))
    override val uiState: State<AuthVM.AuthPageState> = _uiState

    override val eventFlow: SharedFlow<AuthVM.AuthState>
        get() = MutableSharedFlow()

    init {
        _uiState.value = _uiState.value.copy(
            authState = AuthVM.AuthState.SignedIn
//            authState = AuthVM.AuthState.Loading
//            authState = AuthVM.AuthState.SignedOut
        )
    }

    override fun signOut(onCompleteSignOutCallback: () -> Unit) {

    }

    override fun getSignInIntent(): Intent {
        return Intent()
    }

    override fun checkAuthState() {

    }
}