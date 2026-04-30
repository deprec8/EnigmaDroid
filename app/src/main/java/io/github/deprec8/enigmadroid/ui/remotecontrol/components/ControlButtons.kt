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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.RemoteControlKey
import io.github.deprec8.enigmadroid.model.RemoteControlButtonData

@Composable
fun ControlButtons(
    onKeyClicked: (RemoteControlKey) -> Unit, enabled: Boolean
) {
    controlButtons.forEach { row ->
        Row(Modifier.widthIn(0.dp, 500.dp)) {
            row.forEach { button ->
                RemoteControlButton(
                    button = button,
                    onClick = { onKeyClicked(button.key) },
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    aspectRatio = 1.5f
                )
            }
        }
    }
}

private val controlButtons = listOf(
    listOf(
        RemoteControlButtonData(
            icon = Icons.Default.FastRewind,
            iconLabelRes = R.string.rewind,
            key = RemoteControlKey.Rewind
        ),
        RemoteControlButtonData(
            icon = Icons.Default.PlayArrow,
            iconLabelRes = R.string.play,
            key = RemoteControlKey.Play
        ),
        RemoteControlButtonData(
            icon = Icons.Default.Pause, iconLabelRes = R.string.pause, key = RemoteControlKey.Pause
        ),
        RemoteControlButtonData(
            icon = Icons.Default.FastForward,
            iconLabelRes = R.string.forward,
            key = RemoteControlKey.Forward
        ),
    ), listOf(
        RemoteControlButtonData(
            text = "Tv", key = RemoteControlKey.Tv
        ),
        RemoteControlButtonData(
            icon = Icons.Default.Circle,
            iconLabelRes = R.string.record,
            key = RemoteControlKey.Record
        ),
        RemoteControlButtonData(
            icon = Icons.Default.Stop, iconLabelRes = R.string.stop, key = RemoteControlKey.Stop
        ),
        RemoteControlButtonData(
            text = "Radio", key = RemoteControlKey.Radio
        ),
    )
)