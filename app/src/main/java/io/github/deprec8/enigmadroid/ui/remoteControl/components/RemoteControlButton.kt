/*
 * Copyright (C) 2025-2026 deprec8
 *
 * This file is part of EnigmaDroid.
 *
 * EnigmaDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EnigmaDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EnigmaDroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.deprec8.enigmadroid.ui.remoteControl.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.model.RemoteControlButton

@Composable
fun RemoteControlButton(
    button: RemoteControlButton,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    iconTint: Color? = null,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    aspectRatio: Float = 2f
) {
    FilledTonalButton(
        onClick = onClick,
        contentPadding = PaddingValues(),
        enabled = enabled,
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(aspectRatio),
        shape = shape,
        colors = if (containerColor != Color.Unspecified || contentColor != Color.Unspecified) {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = containerColor, contentColor = contentColor
            )
        } else {
            ButtonDefaults.filledTonalButtonColors()
        }
    ) {
        button.text?.let { text ->
            Text(text = text, textAlign = TextAlign.Center)
        }
        button.icon?.let { icon ->
            Icon(
                icon, contentDescription = button.iconLabel, tint = if (!enabled) {
                    ButtonDefaults.filledTonalButtonColors().disabledContentColor
                } else {
                    iconTint ?: LocalContentColor.current
                }
            )
        }
    }
}