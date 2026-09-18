package com.shiny.raisingmoney.core.designsystem.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape

/**
 * Ripple 인디케이션 없는 [clickable].
 */
@Composable
fun Modifier.noRippleClick(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    enabled = enabled,
    onClick = onClick,
)

/**
 * Ripple 인디케이션 있는 [clickable].
 */
@Composable
fun Modifier.rippleClick(
    enabled: Boolean = true,
    shape: Shape? = null,
    onClick: () -> Unit,
): Modifier = this
    .then(
        if (shape == null) {
            this
        } else {
            Modifier.clip(shape)
        }
    )
    .clickable(
        enabled = enabled,
        onClick = onClick,
    )