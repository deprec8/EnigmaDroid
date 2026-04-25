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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.RCButton
import io.github.deprec8.enigmadroid.model.RemoteControlButton

@Composable
fun ArrowButtons(
    onButtonClicked: (RCButton) -> Unit, enabled: Boolean
) {

    val arrowButtons = listOf(
        listOf(
            RemoteControlButton(
                text = "PVR", button = RCButton.PVR
            ),
            RemoteControlButton(
                icon = Icons.Default.KeyboardArrowUp,
                iconLabel = stringResource(R.string.arrow_up),
                button = RCButton.ARROW_UP
            ),
            RemoteControlButton(
                text = "MENU", button = RCButton.MENU
            ),
        ), listOf(
            RemoteControlButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                iconLabel = stringResource(R.string.arrow_left),
                button = RCButton.ARROW_LEFT
            ),
            RemoteControlButton(
                text = "OK", button = RCButton.OK
            ),
            RemoteControlButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                iconLabel = stringResource(R.string.arrow_right),
                button = RCButton.ARROW_RIGHT
            ),
        ), listOf(
            RemoteControlButton(
                text = "EPG", button = RCButton.EPG
            ),
            RemoteControlButton(
                icon = Icons.Default.KeyboardArrowDown,
                iconLabel = stringResource(R.string.arrow_down),
                button = RCButton.ARROW_DOWN
            ),
            RemoteControlButton(
                text = "EXIT", button = RCButton.EXIT
            ),
        )
    )
    arrowButtons.forEach { row ->
        Row(
            Modifier.widthIn(0.dp, 500.dp)
        ) {
            row.forEach { button ->
                RemoteButton(
                    button = button,
                    onClick = { onButtonClicked(button.button) },
                    enabled = enabled,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}