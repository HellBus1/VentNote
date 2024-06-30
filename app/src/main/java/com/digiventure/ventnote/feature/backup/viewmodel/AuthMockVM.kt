package com.digiventure.ventnote.feature.backup.viewmodel

import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AuthMockVM: ViewModel(), AuthBaseVM {
    override val uiState: State<AuthVM.AuthPageState>
        get() = mutableStateOf(AuthVM.AuthPageState(AuthVM.AuthState.SignedIn))
    override val eventFlow: SharedFlow<AuthVM.AuthState>
        get() = MutableSharedFlow()

    override fun signOut(onCompleteSignOutCallback: () -> Unit) {

    }

    override fun getSignInIntent(): Intent {
        return Intent()
    }

    override fun checkAuthState() {

    }
}