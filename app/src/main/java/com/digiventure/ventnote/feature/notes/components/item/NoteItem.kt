package com.digiventure.ventnote.feature.notes.components.item

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.commons.DateUtil
import com.digiventure.ventnote.commons.richtext.MarkdownParser
import com.digiventure.ventnote.components.navbar.TopNavBarIcon
import com.digiventure.ventnote.data.persistence.NoteModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesItem(
    isMarking: Boolean,
    isMarked: Boolean,
    data: NoteModel,
    noteViewMode: String = Constants.VIEW_MODE_LIST,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCheckClick: () -> Unit
) {
    val overallItemShape = MaterialTheme.shapes.medium
    val titleContainerShape = MaterialTheme.shapes.small
    val descriptionContainerShape = MaterialTheme.shapes.small

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Note item ${data.id}" }
            .clip(overallItemShape)
            .background(MaterialTheme.colorScheme.surface)
            .combinedClickable(
                onClick = { if (isMarking) onCheckClick() else onClick() },
                onLongClick = { onLongClick() }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp, 12.dp, 2.dp, 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isMarked) {
                    TopNavBarIcon(
                        image = Icons.Rounded.Check,
                        "",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(16.dp)
                            .semantics { }) {
                    }
                }
                Text(
                    text = MarkdownParser.parseToAnnotatedString(data.title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                val descriptionMaxLines = if (noteViewMode == Constants.VIEW_MODE_STAGGERED) 8 else 4
                Column {
                    Text(
                        text = MarkdownParser.parseToAnnotatedString(data.note),
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Normal,
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = DateUtil.formatNoteDate(data.updatedAt),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
            }
        }
    }
}

