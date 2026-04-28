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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.RemoteControlKey
import io.github.deprec8.enigmadroid.model.RemoteControlButtonData

@Composable
fun ArrowButtons(
    onKeyClicked: (RemoteControlKey) -> Unit, enabled: Boolean
) {
    arrowButtons.forEach { row ->
        Row(
            Modifier.widthIn(0.dp, 500.dp)
        ) {
            row.forEach { button ->
                RemoteControlButton(
                    button = button,
                    onClick = { onKeyClicked(button.key) },
                    enabled = enabled,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}

private val arrowButtons = listOf(
    listOf(
        RemoteControlButtonData(
            text = "Pvr", key = RemoteControlKey.Pvr
        ),
        RemoteControlButtonData(
            icon = Icons.Default.KeyboardArrowUp,
            iconLabelRes = R.string.arrow_up,
            key = RemoteControlKey.Up
        ),
        RemoteControlButtonData(
            text = "Menu", key = RemoteControlKey.Menu
        ),
    ), listOf(
        RemoteControlButtonData(
            icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            iconLabelRes = R.string.arrow_left,
            key = RemoteControlKey.Left
        ),
        RemoteControlButtonData(
            text = "Ok", key = RemoteControlKey.Ok
        ),
        RemoteControlButtonData(
            icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            iconLabelRes = R.string.arrow_right,
            key = RemoteControlKey.Right
        ),
    ), listOf(
        RemoteControlButtonData(
            text = "Epg", key = RemoteControlKey.Epg
        ),
        RemoteControlButtonData(
            icon = Icons.Default.KeyboardArrowDown,
            iconLabelRes = R.string.arrow_down,
            key = RemoteControlKey.Down
        ),
        RemoteControlButtonData(
            text = "EXIT", key = RemoteControlKey.Exit
        ),
    )
)