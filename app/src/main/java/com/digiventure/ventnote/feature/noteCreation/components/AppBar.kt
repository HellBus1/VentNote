package com.digiventure.ventnote.feature.noteCreation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCreationAppBar(
    descriptionTextLength: Int,
    onBackPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = {
            Text(
                text = if(descriptionTextLength > 0) "$descriptionTextLength" else "Add New Note",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 8.dp),
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            TopNavBarIcon(Icons.Filled.ArrowBack, stringResource(R.string.back_nav_icon), Modifier.semantics {  }) {
                onBackPressed()
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