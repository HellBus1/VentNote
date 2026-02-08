package com.digiventure.ventnote.feature.drawer.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun SectionTitle(title: String) {
    val firstLetterColor = MaterialTheme.colorScheme.primary
    val restLetterColor = MaterialTheme.colorScheme.onSurface

    val modifiedTitle = title.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }

    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = firstLetterColor, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp
                )
            ) {
                append(modifiedTitle.first())
            }
            withStyle(
                style = SpanStyle(
                    color = restLetterColor, fontWeight = FontWeight.Bold, fontSize = 22.sp
                )
            ) {
                append(modifiedTitle.substring(1))
            }
        },
        modifier = Modifier.padding(
            start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp
        ),
    )
}
