package com.digiventure.ventnote.feature.notes.components.item

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.commons.DateUtil
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.commons.richtext.MarkdownParser
import com.digiventure.ventnote.components.navbar.TopNavBarIcon
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.feature.tag_manager.components.TagChip

/** Pushpin vector icon drawn programmatically to avoid a drawable resource dependency. */
private val PinIcon: ImageVector
    get() = ImageVector.Builder(
        name = "PinIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(16f, 9f)
            verticalLineTo(4f)
            horizontalLineTo(17f)
            curveTo(17.55f, 4f, 18f, 3.55f, 18f, 3f)
            curveTo(18f, 2.45f, 17.55f, 2f, 17f, 2f)
            horizontalLineTo(7f)
            curveTo(6.45f, 2f, 6f, 2.45f, 6f, 3f)
            curveTo(6f, 3.55f, 6.45f, 4f, 7f, 4f)
            horizontalLineTo(8f)
            verticalLineTo(9f)
            curveTo(8f, 10.86f, 6.21f, 12f, 5f, 12f)
            verticalLineTo(14f)
            horizontalLineTo(11f)
            verticalLineTo(21f)
            lineTo(12f, 22f)
            lineTo(13f, 21f)
            verticalLineTo(14f)
            horizontalLineTo(19f)
            verticalLineTo(12f)
            curveTo(17.79f, 12f, 16f, 10.86f, 16f, 9f)
            close()
        }
    }.build()

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesItem(
    isMarking: Boolean,
    isMarked: Boolean,
    data: NoteModel,
    tags: List<TagModel> = emptyList(),
    noteViewMode: String = Constants.VIEW_MODE_LIST,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCheckClick: () -> Unit,
    /** Called when the user taps the pin icon to toggle pinned state. */
    onPinClick: () -> Unit = {}
) {
    val overallItemShape = MaterialTheme.shapes.medium

    // Animate pin icon tint: primary when pinned, ghost when not
    val pinnedIconTint by animateColorAsState(
        targetValue = if (data.isPinned) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
        animationSpec = tween(durationMillis = 200),
        label = "pin_tint"
    )

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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
                val titleText = if (data.title.isEmpty()) androidx.compose.ui.res.stringResource(com.digiventure.ventnote.R.string.untitled) else data.title
                Text(
                    text = MarkdownParser.parseToAnnotatedString(titleText),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )

                // Pin toggle — always visible in the top-right corner of the card
                IconButton(
                    onClick = onPinClick,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(end = 6.dp)
                        .semantics { testTag = "${TestTags.PIN_ICON_BUTTON}_${data.id}" }
                ) {
                    Icon(
                        imageVector = PinIcon,
                        contentDescription = if (data.isPinned) "Unpin note" else "Pin note",
                        tint = pinnedIconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
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

                    // Tag chips row — display tags directly (maximum 3 tags)
                    if (tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
                        ) {
                            items(items = tags, key = { it.id }) { tag ->
                                TagChip(tag = tag)
                            }
                        }
                    }
                }
            }
        }
    }
}


