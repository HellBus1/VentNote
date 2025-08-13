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
import androidx.compose.material.icons.filled.CheckCircle
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
import com.digiventure.ventnote.commons.DateUtil
import com.digiventure.ventnote.components.navbar.TopNavBarIcon
import com.digiventure.ventnote.data.persistence.NoteModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesItem(
    isMarking: Boolean,
    isMarked: Boolean,
    data: NoteModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCheckClick: () -> Unit
) {
    val overallItemShape = RoundedCornerShape(16.dp)
    val titleContainerShape = RoundedCornerShape(12.dp)
    val descriptionContainerShape = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Note item ${data.id}" }
            .clip(overallItemShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .combinedClickable(
                onClick = { if (isMarking) onCheckClick() else onClick() },
                onLongClick = { onLongClick() }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(titleContainerShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(2.dp, 12.dp, 2.dp, 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isMarked) {
                    TopNavBarIcon(
                        image = Icons.Filled.CheckCircle,
                        "",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(16.dp)
                            .semantics { }) {
                    }
                }
                Text(
                    text = data.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(descriptionContainerShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = data.note,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Normal,
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = DateUtil.convertDateString(
                            "EEEE, MMMM d h:mm a",
                            data.updatedAt.toString()
                        ),
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

