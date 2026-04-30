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

package io.github.deprec8.enigmadroid.ui.remotecontrol.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.RemoteControlKey
import io.github.deprec8.enigmadroid.model.RemoteControlButtonData

@Composable
fun ColorButtons(onKeyClicked: (RemoteControlKey) -> Unit, enabled: Boolean) {
    Row(
        Modifier.widthIn(0.dp, 500.dp)
    ) {
        colorButtons.forEach { button ->
            RemoteControlButton(
                button = button,
                onClick = { onKeyClicked(button.key) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                iconTint = button.iconTint,
                aspectRatio = 1.5f
            )
        }
    }
}

private val colorButtons = listOf(
    RemoteControlButtonData(
        icon = Icons.Default.TripOrigin,
        iconLabelRes = R.string.red,
        iconTint = Color.Red,
        key = RemoteControlKey.Red
    ),
    RemoteControlButtonData(
        icon = Icons.Default.TripOrigin,
        iconLabelRes = R.string.green,
        iconTint = Color.Green,
        key = RemoteControlKey.Green
    ),
    RemoteControlButtonData(
        icon = Icons.Default.TripOrigin,
        iconLabelRes = R.string.yellow,
        iconTint = Color.Yellow,
        key = RemoteControlKey.Yellow
    ),
    RemoteControlButtonData(
        icon = Icons.Default.TripOrigin,
        iconLabelRes = R.string.blue,
        iconTint = Color.Blue,
        key = RemoteControlKey.Blue
    ),
)