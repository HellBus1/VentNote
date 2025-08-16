package com.digiventure.ventnote.feature.note_detail.components.section

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.feature.note_detail.viewmodel.NoteDetailPageBaseVM
import kotlinx.coroutines.delay

@Composable
fun TitleSection(
    viewModel: NoteDetailPageBaseVM,
    isEditingState: Boolean,
    titleTextField: String,
    titleInput: String
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Title,
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

        ImprovedTitleTextField(
            viewModel = viewModel,
            isEditingState = isEditingState,
            titleTextField = titleTextField,
            titleInput = titleInput
        )
    }
}

@Composable
fun ImprovedTitleTextField(
    viewModel: NoteDetailPageBaseVM,
    isEditingState: Boolean,
    titleTextField: String,
    titleInput: String
) {
    val label = "border_color"
    val focusRequester = remember { FocusRequester() }
    val borderColor by animateColorAsState(
        targetValue = if (isEditingState) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(300),
        label = label
    )

    LaunchedEffect(isEditingState) {
        if (isEditingState) {
            delay(200)
            focusRequester.requestFocus()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEditingState) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isEditingState) 4.dp else 0.dp
        ),
        border = BorderStroke(
            width = if (isEditingState) 2.dp else 0.dp,
            color = borderColor
        )
    ) {
        TextField(
            value = viewModel.titleText.value,
            onValueChange = { viewModel.titleText.value = it },
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            singleLine = false,
            readOnly = !isEditingState,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = titleTextField }
                .focusRequester(focusRequester),
            placeholder = {
                if (isEditingState) {
                    Text(
                        text = titleInput,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
        )
    }
}