package com.digiventure.ventnote.feature.backup.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.components.navbar.TopNavBarIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupPageAppBar(
    onBackPressed: () -> Unit,
    onLogoutPressed: () -> Unit,
    onBackupPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.backup_notes),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp),
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        navigationIcon = {
            TopNavBarIcon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_nav_icon), Modifier.semantics {  }) {
                onBackPressed()
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = Modifier.semantics {  },
        actions = {
            TrailingMenuIcons(
                onBackupPressed = onBackupPressed,
                onLogoutPressed = onLogoutPressed
            )
        }
    )
}

@Composable
fun TrailingMenuIcons(
    onLogoutPressed: () -> Unit,
    onBackupPressed: () -> Unit,
) {
    TopNavBarIcon(
        Icons.Filled.CloudUpload,
        stringResource(R.string.backup),
        modifier = Modifier.semantics { }) {
        onBackupPressed()
    }

    TopNavBarIcon(
        Icons.AutoMirrored.Filled.Logout,
        stringResource(R.string.logout_nav_icon),
        modifier = Modifier.semantics { }) {
        onLogoutPressed()
    }
}