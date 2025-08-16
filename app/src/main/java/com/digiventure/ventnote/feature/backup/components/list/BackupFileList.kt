package com.digiventure.ventnote.feature.backup.components.list

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.Constants.EMPTY_STRING
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageBaseVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM
import com.google.api.services.drive.model.File
import kotlinx.coroutines.launch

@Composable
fun BackupFileList(backupPageVM: BackupPageBaseVM,
                   successfullyRestoredRequest: () -> Unit,
                   successfullyDeletedRequest: () -> Unit,
                   onRestoreRequest: (File) -> Unit,
                   onDeleteRequest: (File) -> Unit,
                   onBackupRequest: () -> Unit) {
    val backupPageUiState = backupPageVM.uiState.value
    val driveBackupFileListState = backupPageVM.driveBackupFileList.observeAsState()

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val restoreLoadingDialogState = remember { mutableStateOf(false) }

    val fileRestoreState = backupPageVM.uiState.value.fileRestoreState
    val fileDeleteState = backupPageVM.uiState.value.fileDeleteState

    LaunchedEffect(key1 = true, key2 = backupPageVM.uiState.value.fileDeleteState) {
        scope.launch {
            backupPageVM.getBackupFileList()
        }
    }

    LaunchedEffect(key1 = backupPageVM.uiState.value.fileRestoreState) {
        when (fileRestoreState) {
            is BackupPageVM.FileRestoreState.SyncFailed -> {
                restoreLoadingDialogState.value = false
                val errorMessage = "Restore notes process failed : ${fileRestoreState.errorMessage}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }

            is BackupPageVM.FileRestoreState.SyncFinished -> {
                restoreLoadingDialogState.value = false
                successfullyRestoredRequest()
            }

            is BackupPageVM.FileRestoreState.SyncStarted -> {
                restoreLoadingDialogState.value = true
            }

            else -> {}
        }
    }

    LaunchedEffect(key1 = backupPageVM.uiState.value.fileDeleteState) {
        when (fileDeleteState) {
            is BackupPageVM.FileDeleteState.SyncFailed -> {
                restoreLoadingDialogState.value = false
                val errorMessage = "Delete notes process failed : ${fileDeleteState.errorMessage}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }

            is BackupPageVM.FileDeleteState.SyncFinished -> {
                restoreLoadingDialogState.value = false
                successfullyDeletedRequest()
            }

            is BackupPageVM.FileDeleteState.SyncStarted -> {
                restoreLoadingDialogState.value = true
            }

            else -> {}
        }
    }

    when (backupPageUiState.listOfBackupFileState) {
        is BackupPageVM.FileBackupListState.FileBackupListFinished -> {
            driveBackupFileListState.value.let { backupFiles ->
                if (backupFiles.isNullOrEmpty()) {
                    EmptyBackupListContainer(onBackupRequest)
                }
                else {
                    BackupListContainer(backupFiles, onRestoreRequest, onDeleteRequest, onBackupRequest)
                }
            }
        }

        is BackupPageVM.FileBackupListState.FileBackupListFailed -> {
            BackupFailedContainer(onGetBackupList = {
                scope.launch {
                    backupPageVM.getBackupFileList()
                }
            })
        }

        is BackupPageVM.FileBackupListState.FileBackupListStarted -> {
            FileBackupListStartedContainer()
        }
    }

    if (restoreLoadingDialogState.value) {
        LoadingDialog(
            isOpened = restoreLoadingDialogState.value,
            onDismissCallback = { restoreLoadingDialogState.value = false },
            modifier = Modifier.semantics { testTag = TestTags.LOADING_DIALOG }
        )
    }
}

@Composable
fun EmptyBackupListContainer(
    onBackupRequest: () -> Unit
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
            text = stringResource(R.string.no_backup_found),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.create_your_first_backup),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onBackupRequest()
            },
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(id = R.string.backup_notes),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BackupListContainer(
    backupFiles: List<File>,
    onRestoreRequest: (File) -> Unit,
    onDeleteRequest: (File) -> Unit,
    onBackupRequest: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item(key = "backup_note_button") {
            Button(
                onClick = {
                    onBackupRequest()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.backup_notes),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        items(items = backupFiles) { file ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = EMPTY_STRING },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp,
                    hoveredElevation = 4.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // File icon
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudQueue,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = file.name.substringBefore("."),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onRestoreRequest(file) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudDownload,
                                contentDescription = stringResource(R.string.restore_icon),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = { onDeleteRequest(file) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete_icon),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupFailedContainer(
    onGetBackupList: () -> Unit
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
                    imageVector = Icons.Filled.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Text(
            text = stringResource(R.string.failed_to_load_backups),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onGetBackupList()
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.refresh),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun FileBackupListStartedContainer() {
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
                text = stringResource(R.string.loading_backups),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}