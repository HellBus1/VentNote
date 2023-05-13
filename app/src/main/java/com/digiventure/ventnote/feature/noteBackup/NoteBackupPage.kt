package com.digiventure.ventnote.feature.noteBackup

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.feature.noteBackup.components.ActionImageButton
import com.digiventure.ventnote.feature.noteBackup.components.NoteBackupAppBar
import com.digiventure.ventnote.feature.noteBackup.components.ProfileImage
import com.digiventure.ventnote.feature.noteBackup.viewmodel.NoteBackupPageBaseVM
import com.digiventure.ventnote.feature.noteBackup.viewmodel.NoteBackupPageMockVM
import com.digiventure.ventnote.feature.noteBackup.viewmodel.NoteBackupPageVM
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteBackupPage(
    navHostController: NavHostController,
    viewModel: NoteBackupPageBaseVM = hiltViewModel<NoteBackupPageVM>()
) {
    val googleSignInAccountState = viewModel.googleAccount.observeAsState()

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val rememberedScrollBehavior = remember { scrollBehavior }

    val shareNoteDialogState = remember { mutableStateOf(false) }

    val snackBarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val uploadedDatabase = stringResource(id = R.string.database_is_successfully_backed)

    val context = LocalContext.current

    LaunchedEffect(key1 = googleSignInAccountState.value) {
        if (googleSignInAccountState.value == null) {
            viewModel.googleAccount.value = GoogleSignIn.getLastSignedInAccount(context)
        }
    }

    fun uploadDatabaseToDrive(credential: GoogleAccountCredential) {
        scope.launch {
            viewModel.backupDB(credential)
                .onSuccess {
                    snackBarHostState.showSnackbar(
                        message = uploadedDatabase,
                        withDismissAction = true
                    )
                }
                .onFailure {
                    snackBarHostState.showSnackbar(
                        message = it.message ?: "",
                        withDismissAction = true
                    )
                }
        }
    }

    val signInWithGoogleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val intent = it.data

            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(intent)

            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.googleAccount.value = account
            } catch (e: Exception) {
                Log.e("Google Sign-In", "Sign-in failed with ${e.message}")
            }
        }
    }

    fun handleSignIn() {
        if (googleSignInAccountState.value == null) {
            signInWithGoogleLauncher.launch(viewModel.signInClient.signInIntent)
        } else {
            scope.launch {
                snackBarHostState.showSnackbar(
                    message = "Already logged in",
                    withDismissAction = true
                )
            }
        }
    }

    fun handleDatabaseUpload() {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            setOf(DriveScopes.DRIVE_FILE)
        ).apply {
            selectedAccount = googleSignInAccountState.value?.account
        }

        uploadDatabaseToDrive(credential)
    }

    Scaffold(
        topBar = {
            NoteBackupAppBar(
                onBackPressed = {
                    navHostController.popBackStack()
                },
                onHelpPressed = {
                    shareNoteDialogState.value = true
                },
                scrollBehavior = rememberedScrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProfileImage()
                        Text(text = "Syubban Fakhriya", fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp))
                        Text(text = "syubbanfakhriya@gmail.com", fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp))
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        ActionImageButton(
                            imageVector = Icons.Filled.Login,
                            enabled = true,
                            onClick = { handleSignIn() }
                        )
                        ActionImageButton(
                            imageVector = Icons.Filled.CloudUpload,
                            enabled = googleSignInAccountState.value != null,
                            onClick = { handleDatabaseUpload() }
                        )
                        ActionImageButton(
                            imageVector = Icons.Filled.CloudDownload,
                            enabled = googleSignInAccountState.value != null,
                            onClick = {}
                        )
                        ActionImageButton(
                            imageVector = Icons.Filled.Logout,
                            enabled = googleSignInAccountState.value != null,
                            onClick = { viewModel.logout() }
                        )
                    }
                }
            }
        }
    }

    TextDialog(
        title = stringResource(R.string.information),
        description = stringResource(R.string.share_note_information),
        isOpened = shareNoteDialogState.value,
        onDismissCallback = { shareNoteDialogState.value = false }
    )
}

@Preview
@Composable
fun NoteBackupPagePreview() {
    NoteBackupPage(
        navHostController = rememberNavController(),
        viewModel = NoteBackupPageMockVM()
    )
}