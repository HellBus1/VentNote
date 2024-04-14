package com.digiventure.ventnote.feature.backup.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM

@Composable
fun ListOfBackupFile(backupPageVM: BackupPageVM) {
    val backupPageUiState = backupPageVM.uiState.value
    val driveBackupFileListState = backupPageVM.driveBackupFileList.observeAsState()

    val context = LocalContext.current

    when (val state = backupPageUiState.fileBackupListState) {
        BackupPageVM.FileBackupListState.FileBackupListFinished -> {
            driveBackupFileListState.value.let {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(items = it ?: emptyList()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = it.name ?: Constants.EMPTY_STRING,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                            )

                            Row {
                                Button(
                                    onClick = {  },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudDownload,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                    )
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
        }
        is BackupPageVM.FileBackupListState.FileBackupListStarted -> {
            CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(top = 16.dp))
        }
    }
}