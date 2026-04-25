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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.RCButton
import io.github.deprec8.enigmadroid.model.RemoteControlButton

@Composable
fun ColorButtons(onButtonClicked: (RCButton) -> Unit, enabled: Boolean) {

    val colorButtons = listOf(
        RemoteControlButton(
            icon = Icons.Default.TripOrigin,
            iconLabel = stringResource(R.string.red),
            iconTint = Color.Red,
            button = RCButton.COLOR_RED
        ),
        RemoteControlButton(
            icon = Icons.Default.TripOrigin,
            iconLabel = stringResource(R.string.green),
            iconTint = Color.Green,
            button = RCButton.COLOR_GREEN
        ),
        RemoteControlButton(
            icon = Icons.Default.TripOrigin,
            iconLabel = stringResource(R.string.yellow),
            iconTint = Color.Yellow,
            button = RCButton.COLOR_YELLOW
        ),
        RemoteControlButton(
            icon = Icons.Default.TripOrigin,
            iconLabel = stringResource(R.string.blue),
            iconTint = Color.Blue,
            button = RCButton.COLOR_BLUE
        ),
    )

    Row(
        Modifier.widthIn(0.dp, 500.dp)
    ) {
        colorButtons.forEach { button ->
            RemoteButton(
                button = button,
                onClick = { onButtonClicked(button.button) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                iconTint = button.iconTint,
                aspectRatio = 1.5f
            )
        }
    }
}