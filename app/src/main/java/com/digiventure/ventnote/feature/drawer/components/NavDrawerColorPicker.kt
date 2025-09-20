package com.digiventure.ventnote.feature.drawer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.commons.ColorPalletName
import com.digiventure.ventnote.commons.Constants.EMPTY_STRING
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenLightPrimary
import com.digiventure.ventnote.ui.theme.components.CobaltBlueLightPrimary
import com.digiventure.ventnote.ui.theme.components.CrimsonLightPrimary
import com.digiventure.ventnote.ui.theme.components.PurpleLightPrimary

@Composable
fun NavDrawerColorPicker(
    leftIcon: ImageVector,
    title: String,
    testTagName: String = EMPTY_STRING,
    onColorPicked: (color: Pair<Color, String>) -> Unit
) {
    val colorList = listOf(
        Pair(PurpleLightPrimary, ColorPalletName.PURPLE),
        Pair(CrimsonLightPrimary, ColorPalletName.CRIMSON),
        Pair(CadmiumGreenLightPrimary, ColorPalletName.CADMIUM_GREEN),
        Pair(CobaltBlueLightPrimary, ColorPalletName.COBALT_BLUE)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 8.dp)
            .semantics { testTag = testTagName }, verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(modifier = Modifier.padding(8.dp)) {
                Icon(
                    leftIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(start = 12.dp, bottom = 1.dp)
                .weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Row {
                colorList.forEach {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .width(24.dp)
                        .height(24.dp)
                        .background(it.first)
                        .clickable {
                            onColorPicked(it)
                        })
                    Box(modifier = Modifier.padding(start = 2.dp, end = 2.dp))
                }
            }
        }
    }
}