package com.digiventure.ventnote.feature.backup

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.feature.backup.components.button.SignInButton
import com.digiventure.ventnote.feature.backup.components.list.BackupFileList
import com.digiventure.ventnote.feature.backup.components.navbar.BackupPageAppBar
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
    val deleteConfirmationDialogState = remember { mutableStateOf(false) }
    val stringZero = "0"
    val restoreDataIdState = remember { mutableStateOf(stringZero) }
    val deleteDataIdState = remember { mutableStateOf(stringZero) }

    val context = LocalContext.current

    val backedUpMessage = stringResource(id = R.string.successfully_backed_up)
    val restoredMessage = stringResource(id = R.string.successfully_restored)
    val deletedMessage = stringResource(id = R.string.note_is_successfully_deleted)

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
                val errorMessage = "Backup notes process failed : ${fileBackupState.errorMessage}"
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
                authVM = authViewModel,
                onBackRequest = { navHostController.popBackStack() },
                scrollBehavior = rememberedScrollBehavior,
                onLogoutRequest = {
                    authViewModel.signOut(onCompleteSignOutCallback = {
                        backupPageVM.clearBackupFileList()
                    })
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { contentPadding ->
            Box(
                modifier = Modifier.padding(contentPadding)
                    .padding(start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (authUiState.authState) {
                    AuthVM.AuthState.Loading -> Box(
                        modifier = Modifier.padding(top = 16.dp).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingStateContent()
                    }
                    AuthVM.AuthState.SignedOut -> Box(
                        modifier = Modifier.padding(top = 16.dp).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SignedOutStateContent(
                            authViewModel = authViewModel,
                            onSignInSuccess = {
                                backupPageVM.getBackupFileList()
                            }
                        )
                    }
                    AuthVM.AuthState.SignedIn -> {
                        BackupFileList(
                            backupPageVM = backupPageVM,
                            onRestoreRequest = {
                                restoreDataIdState.value = it.id
                                restoreConfirmationDialogState.value = true
                            },
                            onDeleteRequest = {
                                deleteDataIdState.value = it.id
                                deleteConfirmationDialogState.value = true
                            },
                            successfullyRestoredRequest = {
                                Log.e("hehe event", "restored")
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = restoredMessage,
                                        withDismissAction = true
                                    )
                                }
                            },
                            successfullyDeletedRequest = {
                                Log.e("hehe event", "deleted")
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = deletedMessage,
                                        withDismissAction = true
                                    )
                                }
                            },
                            onBackupRequest = {
                                backupDatabase()
                            }
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    )

    if (loadingDialogState.value) {
        LoadingDialog(
            isOpened = loadingDialogState.value,
            onDismissCallback = { loadingDialogState.value = false },
            modifier = Modifier.semantics { testTag = TestTags.LOADING_DIALOG }
        )
    }

    if (restoreConfirmationDialogState.value) {
        TextDialog(
            title = stringResource(R.string.restore_confirmation_title),
            description = stringResource(R.string.restore_confirmation_description),
            isOpened = restoreConfirmationDialogState.value,
            onDismissCallback = { restoreConfirmationDialogState.value = false },
            onConfirmCallback = {
                val selectedId = restoreDataIdState.value
                if (selectedId != stringZero) {
                    backupPageVM.restoreDatabase(selectedId)
                    restoreConfirmationDialogState.value = false
                }
            }
        )
    }

    if (deleteConfirmationDialogState.value) {
        TextDialog(
            isOpened = deleteConfirmationDialogState.value,
            onDismissCallback = { deleteConfirmationDialogState.value = false },
            onConfirmCallback = {
                val selectedId = deleteDataIdState.value
                if (selectedId != stringZero) {
                    backupPageVM.deleteDatabase(selectedId)
                    deleteConfirmationDialogState.value = false
                }
            }
        )
    }
}

@Composable
private fun LoadingStateContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SignedOutStateContent(
    authViewModel: AuthBaseVM,
    onSignInSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudOff,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Text(
            text = stringResource(R.string.sign_in_to_access_backup),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.connect_with_google_to_backup),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        SignInButton(
            authViewModel = authViewModel,
            signInSuccessCallback = onSignInSuccess
        )
    }
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