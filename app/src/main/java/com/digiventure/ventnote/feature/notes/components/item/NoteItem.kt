package com.digiventure.ventnote.feature.notes.components.item

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.commons.DateUtil
import com.digiventure.ventnote.data.persistence.NoteModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesItem(
    isMarking: Boolean,
    isMarked: Boolean,
    data: NoteModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCheckClick: () -> Unit)
{
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .semantics { contentDescription = "Note item ${data.id}" }
            .combinedClickable(
                onClick = { if (isMarking) onCheckClick() else onClick()  },
                onLongClick = { onLongClick() }
            )
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Box(modifier = Modifier.fillMaxSize()
            .padding(start = if(isMarked) 8.dp else 0.dp)
            .background(MaterialTheme.colorScheme.surface)) {

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = data.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = data.note,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
                Text(
                    text = DateUtil.convertDateString("EEEE, MMMM d h:mm a", data.createdAt.toString()),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}