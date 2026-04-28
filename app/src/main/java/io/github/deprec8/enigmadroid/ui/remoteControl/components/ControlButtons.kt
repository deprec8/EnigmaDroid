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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.RCButton
import io.github.deprec8.enigmadroid.model.RemoteControlButton

@Composable
fun ControlButtons(
    onButtonClicked: (RCButton) -> Unit, enabled: Boolean
) {

    val controlButtons = listOf(
        listOf(
            RemoteControlButton(
                icon = Icons.Default.FastRewind,
                iconLabel = stringResource(R.string.rewind),
                button = RCButton.REWIND
            ),
            RemoteControlButton(
                icon = Icons.Default.PlayArrow,
                iconLabel = stringResource(R.string.play),
                button = RCButton.PLAY
            ),
            RemoteControlButton(
                icon = Icons.Default.Pause,
                iconLabel = stringResource(R.string.pause),
                button = RCButton.PAUSE
            ),
            RemoteControlButton(
                icon = Icons.Default.FastForward,
                iconLabel = stringResource(R.string.forward),
                button = RCButton.FORWARD
            ),
        ), listOf(
            RemoteControlButton(
                text = "TV", button = RCButton.TV
            ),
            RemoteControlButton(
                icon = Icons.Default.Circle,
                iconLabel = stringResource(R.string.record),
                button = RCButton.RECORD
            ),
            RemoteControlButton(
                icon = Icons.Default.Stop,
                iconLabel = stringResource(R.string.stop),
                button = RCButton.STOP
            ),
            RemoteControlButton(
                text = "RADIO", button = RCButton.RADIO
            ),
        )
    )

    controlButtons.forEach { row ->
        Row(Modifier.widthIn(0.dp, 500.dp)) {
            row.forEach { button ->
                RemoteControlButton(
                    button = button,
                    onClick = { onButtonClicked(button.button) },
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    aspectRatio = 1.5f
                )
            }
        }
    }
}