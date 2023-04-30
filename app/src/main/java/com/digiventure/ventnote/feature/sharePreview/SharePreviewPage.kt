package com.digiventure.ventnote.feature.sharePreview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.DateUtil
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.feature.sharePreview.components.SharePreviewAppBar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePreviewPage(
    navHostController: NavHostController,
    note: NoteModel?
) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val rememberedScrollBehavior = remember { scrollBehavior }

    val date = DateUtil.convertDateString("EEE, MMM dd HH:mm yyyy",
        note?.createdAt?.toString() ?: Date().toString()
    )
    val title = note?.title ?: "title"
    val text = note?.note ?: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eget egestas nisi, sit amet tincidunt lorem. Aliquam pharetra, tortor nec bibendum rhoncus, lorem est placerat quam, vitae posuere dui massa eu tortor. Nullam nibh turpis, egestas id sollicitudin nec, placerat vitae libero. Nulla commodo ex orci, et commodo leo tempor vitae. Sed posuere dolor urna, vitae tempus magna lobortis ac. Fusce dignissim eros sit amet velit commodo, ac viverra augue iaculis. Aenean facilisis, est ut gravida feugiat, est sem varius neque, at ultricies lectus nisi sed urna. Ut sagittis orci ac ante convallis eleifend. Nulla nec congue purus, at sagittis felis. Sed mattis quam orci, molestie auctor orci venenatis et. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Sed vestibulum placerat enim eu tempor. Aenean sed augue ut eros hendrerit porttitor.\n"

    Scaffold(
        topBar = {
            SharePreviewAppBar(
                onBackPressed = {
                    navHostController.popBackStack()
                },
                onCopyPressed = {},
                onHelpPressed = {},
                scrollBehavior = rememberedScrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {  },
                modifier = Modifier
                    .semantics {
                        testTag = TestTags.SHARE_NOTE_FAB
                    },
                text = {
                    Text(stringResource(R.string.share_note), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.fab)
                    )
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                Text(
                    date,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(text, fontSize = 18.sp)
            }
        }
    }
}

@Preview
@Composable
fun SharePreviewPagePreview() {
    SharePreviewPage(
        navHostController = rememberNavController(),
        note = null
    )
}