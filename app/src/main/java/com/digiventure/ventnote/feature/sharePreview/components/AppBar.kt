package com.digiventure.ventnote.feature.sharePreview.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.components.navbar.TopNavBarIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePreviewAppBar(
    onBackPressed: () -> Unit,
    onCopyPressed: () -> Unit,
    onHelpPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior) {

    TopAppBar(
        title = {
            Text(
                text = "Share Preview",
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
        actions = {
            TopNavBarIcon(Icons.Default.Help, stringResource(R.string.menu_nav_icon), Modifier.semantics {  }) {
                onHelpPressed()
            }
            TopNavBarIcon(Icons.Default.ContentCopy, stringResource(R.string.menu_nav_icon), Modifier.semantics {  }) {
                onCopyPressed()
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = Modifier.semantics {  },
    )
}