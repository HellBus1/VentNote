package com.digiventure.ventnote.feature.backup

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.feature.backup.components.BackupPageAppBar
import com.digiventure.ventnote.feature.backup.components.ListOfBackupFile
import com.digiventure.ventnote.feature.backup.components.SignInButton
import com.digiventure.ventnote.feature.backup.components.SignedInButtons
import com.digiventure.ventnote.feature.backup.viewmodel.AuthBaseVM
import com.digiventure.ventnote.feature.backup.viewmodel.AuthMockVM
import com.digiventure.ventnote.feature.backup.viewmodel.AuthVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupPage(
    navHostController: NavHostController,
    authViewModel: AuthBaseVM = hiltViewModel<AuthVM>(),
    backupPageVM: BackupPageVM = hiltViewModel<BackupPageVM>()
) {
    val authUiState = authViewModel.uiState.value

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val rememberedScrollBehavior = remember { scrollBehavior }

    val loadingDialog = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        scope.launch {
            backupPageVM.backupFileList()
        }
    }

    val fileSyncState = backupPageVM.uiState.value.fileSyncState
    LaunchedEffect(key1 = fileSyncState) {
        when(fileSyncState) {
            is BackupPageVM.FileSyncState.SyncFailed -> {
                loadingDialog.value = false
                val errorMessage = fileSyncState.errorMessage
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            BackupPageVM.FileSyncState.SyncFinished -> {
                loadingDialog.value = false
            }
            BackupPageVM.FileSyncState.SyncStarted -> {
                loadingDialog.value = true
            }
        }
    }

    Scaffold(
        topBar = {
            BackupPageAppBar(
                onBackPressed = { navHostController.popBackStack() },
                scrollBehavior = rememberedScrollBehavior
            )
        },
        content = { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    when (authUiState.authState) {
                        AuthVM.AuthState.Loading -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        AuthVM.AuthState.SignedOut -> SignInButton(authViewModel)
                        AuthVM.AuthState.SignedIn -> SignedInButtons(authViewModel, backupPageVM)
                    }
                    Spacer(modifier = Modifier.padding(6.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    ListOfBackupFile(backupPageVM = backupPageVM)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    LoadingDialog(isOpened = loadingDialog.value, onDismissCallback = { loadingDialog.value = false },
        modifier = Modifier.semantics { testTag = TestTags.LOADING_DIALOG })


}

@Preview
@Composable
fun BackupPagePreview() {
    BackupPage(
        navHostController = rememberNavController(),
        authViewModel = AuthMockVM()
    )
}