/*
 * Copyright (C) 2025 deprec8
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

package io.github.deprec8.enigmadroid.ui.remoteControl.modules

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.RemoteControlButton
import io.github.deprec8.enigmadroid.ui.remoteControl.RemoteControlViewModel

@Composable
fun ArrowButtons(remoteControlViewModel: RemoteControlViewModel, enabled: Boolean, performHaptic: () -> Unit) {

    val arrowButtons = listOf(
        listOf(
            RemoteControlButton(
                text = "PVR",
                onClick = remoteControlViewModel::pvr
            ),
            RemoteControlButton(
                icon = Icons.Default.KeyboardArrowUp,
                iconLabel = stringResource(R.string.arrow_up),
                onClick = remoteControlViewModel::aUP
            ),
            RemoteControlButton(
                text = "MENU",
                onClick = remoteControlViewModel::menu
            ),
        ), listOf(
            RemoteControlButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                iconLabel = stringResource(R.string.arrow_left),
                onClick = remoteControlViewModel::aLeft
            ),
            RemoteControlButton(
                text = "OK",
                onClick = remoteControlViewModel::ok
            ),
            RemoteControlButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                iconLabel = stringResource(R.string.arrow_right),
                onClick = remoteControlViewModel::aRight
            ),
        ), listOf(
            RemoteControlButton(
                text = "EPG",
                onClick = remoteControlViewModel::epg
            ),
            RemoteControlButton(
                icon = Icons.Default.KeyboardArrowDown,
                iconLabel = stringResource(R.string.arrow_down),
                onClick = remoteControlViewModel::aDown
            ),
            RemoteControlButton(
                text = "EXIT",
                onClick = remoteControlViewModel::exit
            ),
        )
    )
    arrowButtons.forEach { row ->
        Row(
            Modifier
                .widthIn(0.dp, 450.dp)
        ) {
            row.forEach { button ->
                FilledTonalButton(
                    onClick = {
                        button.onClick()
                        performHaptic()
                    },
                    contentPadding = PaddingValues(),
                    enabled = enabled,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                        .aspectRatio(2f),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    button.text?.let { text ->
                        Text(text = text, textAlign = TextAlign.Center)
                    }
                    button.icon?.let { icon ->
                        Icon(
                            icon,
                            contentDescription = button.iconLabel
                        )
                    }
                }
            }
        }
    }

}