package com.digiventure.ventnote.feature.share_preview

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.DateUtil
import com.digiventure.ventnote.components.LockScreenOrientation
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.feature.share_preview.components.navbar.EnhancedBottomAppBar
import com.digiventure.ventnote.feature.share_preview.components.navbar.SharePreviewAppBar
import com.digiventure.ventnote.feature.share_preview.components.sheets.ShareSheet
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePreviewPage(
    navHostController: NavHostController,
    note: NoteModel?
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val rememberedScrollBehavior = remember { scrollBehavior }

    val shareNoteDialogState = remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    val date = remember(note?.createdAt) {
        try {
            DateUtil.convertDateString(
                "EEE, MMM dd HH:mm yyyy",
                note?.createdAt?.toString() ?: Date().toString()
            )
        } catch (e: Exception) {
            DateUtil.convertDateString("EEE, MMM dd HH:mm yyyy", Date().toString())
        }
    }

    val title = note?.title?.takeIf { it.isNotBlank() } ?: stringResource(R.string.empty_note_title_placeholder)
    val text = note?.note?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.empty_note_placeholder)

    val joinedText = remember(date, title, text) {
        buildString {
            append(date)
            append("\n\n")
            append(title)
            append("\n\n")
            append(text)
        }
    }

    val openBottomSheet = rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val haptic = LocalHapticFeedback.current

    val handleShare = remember {
        {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            coroutineScope.launch {
                try {
                    shareText(joinedText, context)
                } catch (e: Exception) {
                    snackBarHostState.showSnackbar(
                        message = context.getString(R.string.failed_to_share_note),
                        duration = SnackbarDuration.Short
                    )
                } finally {
                    openBottomSheet.value = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SharePreviewAppBar(
                onBackPressed = {
                    navHostController.navigateUp()
                },
                onHelpPressed = {
                    shareNoteDialogState.value = true
                },
                scrollBehavior = rememberedScrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        content = {
            Box(modifier = Modifier.padding(it)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 20.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = date,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    ),
                                )
                            }
                        }
                    }

                    item {
                        SelectionContainer {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            SelectionContainer {
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                    ),
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            EnhancedBottomAppBar(
                onCancelClick = { openBottomSheet.value = true },
            )
        }
    )

    if (shareNoteDialogState.value) {
        TextDialog(
            title = stringResource(R.string.information),
            description = stringResource(R.string.share_note_information),
            isOpened = true,
            onDismissCallback = { shareNoteDialogState.value = false },
            modifier = Modifier.semantics { }
        )
    }

    if (openBottomSheet.value) {
        ShareSheet(
            isOpened = openBottomSheet.value,
            bottomSheetState = bottomSheetState,
            onDismissRequest = {
                openBottomSheet.value = false
            },
            onShareRequest = {
                handleShare()
            }
        )
    }

    BackHandler {
        navHostController.navigateUp()
    }
}

fun shareText(text: String, context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_note))
    context.startActivity(shareIntent)
}

@Preview
@Composable
fun SharePreviewPagePreview() {
    SharePreviewPage(
        navHostController = rememberNavController(),
        note = null
    )
}