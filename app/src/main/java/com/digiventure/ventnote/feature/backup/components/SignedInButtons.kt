package com.digiventure.ventnote.feature.backup.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.feature.backup.viewmodel.AuthBaseVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM
import kotlinx.coroutines.launch

@Composable
fun SignedInButtons(
    authViewModel: AuthBaseVM,
    backupPageVM: BackupPageVM,
    signOutSuccessCallback: () -> Unit
) {
    val scope = rememberCoroutineScope()

    fun backupDatabase() {
        scope.launch {
            backupPageVM.backupDatabase()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                authViewModel.signOut(onCompleteSignOutCallback = {
                    signOutSuccessCallback()
                })
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.sign_out),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = { backupDatabase() },
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CloudUpload,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}