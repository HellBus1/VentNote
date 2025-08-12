package com.digiventure.ventnote.feature.note_creation.components.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.digiventure.ventnote.R
import com.digiventure.ventnote.components.navbar.TopNavBarIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCreationAppBar(
    onBackPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.add_new_note),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        navigationIcon = {
            TopNavBarIcon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_nav_icon), Modifier.semantics {  }) {
                onBackPressed()
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = Modifier.semantics {  },
    )
}