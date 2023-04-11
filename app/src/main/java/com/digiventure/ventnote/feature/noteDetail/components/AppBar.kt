package com.digiventure.ventnote.feature.noteDetail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailAppBar(
    isEditing: Boolean,
    descriptionTextLength: Int,
    onBackPressed: () -> Unit,
    onClosePressed: () -> Unit,
    onDeletePressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior) {
    val expanded = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = if(isEditing) "$descriptionTextLength" else "Note Detail",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 8.dp),
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            if (isEditing) {
                TopNavBarIcon(Icons.Filled.Close, stringResource(R.string.back_nav_icon), Modifier.semantics {  }) {
                    onClosePressed()
                }
            } else {
                TopNavBarIcon(Icons.Filled.ArrowBack, stringResource(R.string.back_nav_icon), Modifier.semantics {  }) {
                    onBackPressed()
                }
            }
        },
        actions = {
            TopNavBarIcon(Icons.Filled.MoreVert, stringResource(R.string.menu_nav_icon), Modifier.semantics {  }) {
                expanded.value = true
            }
            DropdownMenu(
                offset = DpOffset((10).dp, 0.dp),
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }) {
                DropdownMenuItem(
                    text = { Text(
                        text = "Delete Note",
                        fontSize = 16.sp,
                        modifier = Modifier.semantics {  })
                    },
                    onClick = {
                        onDeletePressed()
                        expanded.value = false
                    },
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = Modifier.semantics {  },
    )
}

@Composable
fun TopNavBarIcon(
    image: ImageVector,
    description: String,
    modifier: Modifier,
    tint: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit,
) {
    IconButton(onClick = { onClick() }, modifier = modifier) {
        Icon(
            imageVector = image,
            contentDescription = description,
            tint = tint,
        )
    }
}