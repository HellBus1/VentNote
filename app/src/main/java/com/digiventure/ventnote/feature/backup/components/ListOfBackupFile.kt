package com.digiventure.ventnote.feature.backup.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageBaseVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM
import kotlinx.coroutines.launch

@Composable
fun ListOfBackupFile(backupPageVM: BackupPageBaseVM, successfullyRestoredCallback: () -> Unit) {
    val backupPageUiState = backupPageVM.uiState.value
    val driveBackupFileListState = backupPageVM.driveBackupFileList.observeAsState()

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val loadingDialog = remember { mutableStateOf(false) }

    val fileRestoreState = backupPageVM.uiState.value.fileRestoreState
    val fileDeleteState = backupPageVM.uiState.value.fileDeleteState

    LaunchedEffect(key1 = true, key2 = backupPageVM.uiState.value.fileDeleteState) {
        scope.launch {
            backupPageVM.getBackupFileList()
        }
    }

    LaunchedEffect(
        key1 = backupPageVM.uiState.value.fileRestoreState,
        key2 = backupPageVM.uiState.value.fileDeleteState
    ) {
        when {
            fileRestoreState is BackupPageVM.FileRestoreState.SyncFailed ||
                    fileDeleteState is BackupPageVM.FileDeleteState.SyncFailed -> {
                loadingDialog.value = false
                val errorMessage = when {
                    fileRestoreState is BackupPageVM.FileRestoreState.SyncFailed -> fileRestoreState.errorMessage
                    fileDeleteState is BackupPageVM.FileDeleteState.SyncFailed -> fileDeleteState.errorMessage
                    else -> ""
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }

            fileRestoreState is BackupPageVM.FileRestoreState.SyncFinished ||
                    fileDeleteState is BackupPageVM.FileDeleteState.SyncFinished -> {
                loadingDialog.value = false
                successfullyRestoredCallback()
            }

            fileRestoreState is BackupPageVM.FileRestoreState.SyncStarted ||
                    fileDeleteState is BackupPageVM.FileDeleteState.SyncStarted -> {
                loadingDialog.value = true
            }

            else -> {}
        }
    }

    when (val state = backupPageUiState.fileBackupListState) {
        BackupPageVM.FileBackupListState.FileBackupListFinished -> {
            driveBackupFileListState.value.let {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    val shape = RoundedCornerShape(12.dp)

                    items(items = it ?: emptyList()) {
                        Box(
                            modifier = Modifier
                                .semantics { contentDescription = "" }
                                .clip(shape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            val dotDelimiter = "."
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = it.name.substringBefore(dotDelimiter),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp),
                                )
                                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                Row {
                                    OutlinedButton (
                                        onClick = { backupPageVM.restoreDatabase(it.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(
                                            horizontal = 2.dp,
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CloudDownload,
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                    OutlinedButton(
                                        onClick = { backupPageVM.deleteDatabase(it.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(
                                            horizontal = 2.dp,
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        is BackupPageVM.FileBackupListState.FileBackupListFailed -> {
            val errorMessage = state.errorMessage
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()

            OutlinedButton(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    scope.launch {
                        backupPageVM.getBackupFileList()
                    }
                },
                contentPadding = PaddingValues(
                    horizontal = 2.dp,
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.refresh),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        is BackupPageVM.FileBackupListState.FileBackupListStarted -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 16.dp)
            )
        }
    }

    LoadingDialog(isOpened = loadingDialog.value,
        onDismissCallback = { loadingDialog.value = false },
        modifier = Modifier.semantics { testTag = TestTags.LOADING_DIALOG })
}