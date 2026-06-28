package com.digiventure.ventnote.feature.tag_manager

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.feature.tag_manager.components.TagChip
import com.digiventure.ventnote.feature.tag_manager.components.TagEditorDialog
import com.digiventure.ventnote.feature.tag_manager.viewmodel.TagManagerBaseVM
import com.digiventure.ventnote.feature.tag_manager.viewmodel.TagManagerMockVM
import com.digiventure.ventnote.feature.tag_manager.viewmodel.TagManagerVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagerPage(
    navHostController: NavHostController,
    viewModel: TagManagerBaseVM = hiltViewModel<TagManagerVM>()
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val tagListState by viewModel.tagList.observeAsState()
    val tags = tagListState?.getOrNull() ?: emptyList()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingTag by remember { mutableStateOf<TagModel?>(null) }
    var deletingTag by remember { mutableStateOf<TagModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.observeTags()
    }

    LaunchedEffect(tagListState) {
        tagListState?.onFailure { error ->
            snackBarHostState.showSnackbar(error.message ?: "Error loading tags")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Tags",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                text = {
                    Text(
                        text = "New Tag",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                },
                icon = {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = "Create tag")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                shape = MaterialTheme.shapes.medium
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { contentPadding ->
        if (tags.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🏷️",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tags yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Create a tag to start organizing your notes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(items = tags, key = { it.id }) { tag ->
                    TagListItem(
                        tag = tag,
                        onEdit = { editingTag = tag },
                        onDelete = { deletingTag = tag }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }

    // Create tag dialog
    if (showCreateDialog) {
        TagEditorDialog(
            onConfirm = { newTag ->
                showCreateDialog = false
                scope.launch {
                    viewModel.insertTag(newTag).onFailure { e ->
                        snackBarHostState.showSnackbar(e.message ?: "Failed to create tag")
                    }
                }
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    // Edit tag dialog
    editingTag?.let { tag ->
        TagEditorDialog(
            existingTag = tag,
            onConfirm = { updated ->
                editingTag = null
                scope.launch {
                    viewModel.updateTag(updated).onFailure { e ->
                        snackBarHostState.showSnackbar(e.message ?: "Failed to update tag")
                    }
                }
            },
            onDismiss = { editingTag = null }
        )
    }

    // Delete confirmation dialog
    deletingTag?.let { tag ->
        TextDialog(
            isOpened = true,
            title = "Delete Tag",
            description = "Delete \"${tag.name}\"? Notes with this tag will become uncategorized.",
            onDismissCallback = { deletingTag = null },
            onConfirmCallback = {
                deletingTag = null
                scope.launch {
                    viewModel.deleteTag(tag).onFailure { e ->
                        snackBarHostState.showSnackbar(e.message ?: "Failed to delete tag")
                    }
                }
            }
        )
    }
}

@Composable
private fun TagListItem(
    tag: TagModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TagChip(tag = tag, modifier = Modifier.weight(1f, fill = false))

        Row {
            IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit tag ${tag.name}",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete tag ${tag.name}",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun TagManagerPagePreview() {
    TagManagerPage(
        navHostController = rememberNavController(),
        viewModel = TagManagerMockVM()
    )
}
