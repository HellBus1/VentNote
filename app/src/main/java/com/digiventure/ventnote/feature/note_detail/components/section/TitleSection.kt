package com.digiventure.ventnote.feature.note_detail.components.section

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.commons.richtext.RichTextEditor
import com.digiventure.ventnote.commons.richtext.RichTextState

@Composable
fun TitleSection(
    titleRichTextState: RichTextState,
    isEditingState: Boolean,
    titleTextField: String,
    titleInput: String,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.sort_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        RichTextEditor(
            richTextState = titleRichTextState,
            isEditing = isEditingState,
            readOnly = !isEditingState,
            placeholder = if (isEditingState) titleInput else "",
            contentDescriptionText = titleTextField,
            testTagText = TestTags.TITLE_TEXT_FIELD,
            minHeight = 56.dp,
            onFocusChanged = onFocusChanged
        )
    }
}