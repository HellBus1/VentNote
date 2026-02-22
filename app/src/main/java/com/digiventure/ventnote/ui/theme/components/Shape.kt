package com.digiventure.ventnote.ui.theme.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Defines the flat shapes for the application.
 * In a flat design, components generally use solid edges or slight, uniform rounding
 * instead of full pills. Here we use 8.dp to signify a very subtle rounded box look.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp)
)
