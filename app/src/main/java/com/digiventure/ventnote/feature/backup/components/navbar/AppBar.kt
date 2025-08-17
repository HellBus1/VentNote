package com.digiventure.ventnote.feature.backup.components.navbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.components.navbar.TopNavBarIcon
import com.digiventure.ventnote.feature.backup.viewmodel.AuthBaseVM
import com.digiventure.ventnote.feature.backup.viewmodel.AuthVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupPageAppBar(
    authVM: AuthBaseVM,
    onBackRequest: () -> Unit,
    onLogoutRequest: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior) {

    val authUiState = authVM.uiState.value

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.backup_notes),
                modifier = Modifier.padding(start = 4.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        navigationIcon = {
            TopNavBarIcon(
                Icons.AutoMirrored.Filled.ArrowBack,
                stringResource(R.string.backup_nav_icon),
                Modifier.semantics {  }) {
                onBackRequest()
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = Modifier.semantics {  },
        actions = {
            if (authUiState.authState == AuthVM.AuthState.SignedIn) {
                TrailingMenuIcons(
                    onLogoutRequest = onLogoutRequest
                )
            }
        }
    )
}

@Composable
fun TrailingMenuIcons(
    onLogoutRequest: () -> Unit,
) {
    TopNavBarIcon(
        Icons.AutoMirrored.Filled.Logout,
        stringResource(R.string.logout_nav_icon),
        modifier = Modifier.semantics { }) {
        onLogoutRequest()
    }
}