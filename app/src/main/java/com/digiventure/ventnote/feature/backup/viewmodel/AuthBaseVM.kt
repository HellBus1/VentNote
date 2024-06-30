package com.digiventure.ventnote.feature.backup.viewmodel

import android.content.Intent
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.SharedFlow

interface AuthBaseVM {
    /**
     * Determine auth state
     * */
    val uiState: State<AuthVM.AuthPageState>
    /**
     * Emit auth state to ui
     * */
    val eventFlow: SharedFlow<AuthVM.AuthState>

    /**
     * Sign out
     * */
    fun signOut(onCompleteSignOutCallback: () -> Unit)
    /**
     * To prompt google sign in page manually
     * */
    fun getSignInIntent(): Intent
    /**
     * To check whether user is already logged in or not
     * control ui state
     * */
    fun checkAuthState()
}