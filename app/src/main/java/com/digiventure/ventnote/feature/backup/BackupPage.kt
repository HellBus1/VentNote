package com.digiventure.ventnote.feature.backup

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.feature.backup.components.BackupPageAppBar
import com.digiventure.ventnote.feature.backup.components.ListOfBackupFile
import com.digiventure.ventnote.feature.backup.components.SignInButton
import com.digiventure.ventnote.feature.backup.viewmodel.AuthBaseVM
import com.digiventure.ventnote.feature.backup.viewmodel.AuthMockVM
import com.digiventure.ventnote.feature.backup.viewmodel.AuthVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageBaseVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageMockVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupPage(
    navHostController: NavHostController,
    authViewModel: AuthBaseVM = hiltViewModel<AuthVM>(),
    backupPageVM: BackupPageBaseVM = hiltViewModel<BackupPageVM>()
) {
    val authUiState = authViewModel.uiState.value

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val rememberedScrollBehavior = remember { scrollBehavior }
    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    val loadingDialogState = remember { mutableStateOf(false) }
    val restoreConfirmationDialogState = remember { mutableStateOf(false) }
    val restoreDataIdState = remember { mutableStateOf("0") }

    val context = LocalContext.current

    val backedUpMessage = stringResource(id = R.string.successfully_backed_up)
    val restoredMessage = stringResource(id = R.string.successfully_updated)

    fun backupDatabase() {
        scope.launch {
            backupPageVM.backupDatabase()
        }
    }

    val fileBackupState = backupPageVM.uiState.value.fileBackupState
    LaunchedEffect(key1 = fileBackupState) {
        when(fileBackupState) {
            is BackupPageVM.FileBackupState.SyncFailed -> {
                loadingDialogState.value = false
                val errorMessage = fileBackupState.errorMessage
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            BackupPageVM.FileBackupState.SyncFinished -> {
                loadingDialogState.value = false
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = backedUpMessage,
                        withDismissAction = true
                    )
                }
            }
            BackupPageVM.FileBackupState.SyncStarted -> {
                loadingDialogState.value = true
            }
            BackupPageVM.FileBackupState.SyncInitial -> {}
        }
    }

    Scaffold(
        topBar = {
            BackupPageAppBar(
                onBackPressed = { navHostController.popBackStack() },
                scrollBehavior = rememberedScrollBehavior,
                onBackupPressed = {
                    backupDatabase()
                },
                onLogoutPressed = {
                    authViewModel.signOut(onCompleteSignOutCallback = {
                        backupPageVM.getBackupFileList()
                    })
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { contentPadding ->
            Box(
                modifier = Modifier.padding(contentPadding).padding(start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (authUiState.authState) {
                    AuthVM.AuthState.Loading -> Box(
                        modifier = Modifier.padding(top = 16.dp).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                    AuthVM.AuthState.SignedOut -> Box(
                        modifier = Modifier.padding(top = 16.dp).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SignInButton(authViewModel, signInSuccessCallback = {
                            backupPageVM.getBackupFileList()
                        })
                    }
                    AuthVM.AuthState.SignedIn -> {
                        ListOfBackupFile(
                            backupPageVM = backupPageVM,
                            onRestoreCallback = {
                                restoreDataIdState.value = it.id
                                restoreConfirmationDialogState.value = true
                            },
                            successfullyRestoredCallback = {
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = restoredMessage,
                                        withDismissAction = true
                                    )
                                }
                            }
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    )

    LoadingDialog(
        isOpened = loadingDialogState.value,
        onDismissCallback = { loadingDialogState.value = false },
        modifier = Modifier.semantics { testTag = TestTags.LOADING_DIALOG }
    )

    TextDialog(
        title = stringResource(R.string.restore_confirmation_title),
        description = stringResource(R.string.restore_confirmation_description),
        isOpened = restoreConfirmationDialogState.value,
        onDismissCallback = { restoreConfirmationDialogState.value = false },
        onConfirmCallback = {
            val selectedId = restoreDataIdState.value;
            if (selectedId != "0") {
                backupPageVM.restoreDatabase(selectedId)
                restoreConfirmationDialogState.value = false
            }
        }
    )
}

@Preview(device = "id:pixel_xl")
@Composable
fun BackupPagePreview() {
    BackupPage(
        navHostController = rememberNavController(),
        authViewModel = AuthMockVM(),
        backupPageVM = BackupPageMockVM()
    )
}