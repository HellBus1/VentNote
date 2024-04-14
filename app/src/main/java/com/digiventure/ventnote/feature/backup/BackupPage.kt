package com.digiventure.ventnote.feature.backup

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.feature.backup.components.BackupPageAppBar
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

    LaunchedEffect(key1 = true) {
        scope.launch {
            backupPageVM.listOfBackupFiles()
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

//                    backupPageVM.backupFileList.value.let {
//                        if (it != null && it.isSuccess) {
//                            val fileList = it.getOrNull()?.files
//                            LazyColumn(
//                                modifier = Modifier.fillMaxSize(),
//                                verticalArrangement = Arrangement.spacedBy(16.dp),
//                            ) {
//                                items(items = fileList?.toList() ?: emptyList()) {
//                                    Box {
//                                        Text(text = it.id)
//                                        Text(text = it.description)
//                                        Text(text = it.name)
//                                        Text(text = it.kind)
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    LoadingDialog(isOpened = loadingDialog.value, onDismissCallback = { loadingDialog.value = false },
        modifier = Modifier.semantics { testTag = TestTags.LOADING_DIALOG })
}

@Composable
private fun SignInButton(authViewModel: AuthBaseVM) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            authViewModel.checkAuthState()
        } else {
            Toast.makeText(context, "Auth Failed", Toast.LENGTH_LONG).show()
        }
    }

    Button(
        onClick = { launcher.launch(authViewModel.getSignInIntent()) },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            )
            Text(
                text = stringResource(id = R.string.sign_in_with_google),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
private fun SignedInButtons(authViewModel: AuthBaseVM, backupPageVM: BackupPageVM) {
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
            onClick = { authViewModel.signOut() },
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

@Preview
@Composable
fun BackupPagePreview() {
    BackupPage(
        navHostController = rememberNavController(),
        authViewModel = AuthMockVM()
    )
}