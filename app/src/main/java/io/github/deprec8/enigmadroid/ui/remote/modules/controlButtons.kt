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

package io.github.deprec8.enigmadroid.ui.remote.modules

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
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
import io.github.deprec8.enigmadroid.model.RemoteButton
import io.github.deprec8.enigmadroid.ui.remote.RemoteViewModel

@Composable
fun ControlButtons(remoteViewModel: RemoteViewModel, enabled: Boolean, performHaptic: () -> Unit) {

    val controlButtons = listOf(
        listOf(
            RemoteButton(
                icon = Icons.Default.FastRewind,
                iconLabel = stringResource(R.string.rewind),
                onClick = remoteViewModel::rewind
            ),
            RemoteButton(
                icon = Icons.Default.PlayArrow,
                iconLabel = stringResource(R.string.play),
                onClick = remoteViewModel::play
            ),
            RemoteButton(
                icon = Icons.Default.Pause,
                iconLabel = stringResource(R.string.pause),
                onClick = remoteViewModel::pause
            ),
            RemoteButton(
                icon = Icons.Default.FastForward,
                iconLabel = stringResource(R.string.forward),
                onClick = remoteViewModel::forward
            ),
        ), listOf(
            RemoteButton(
                text = "TV",
                onClick = remoteViewModel::tv
            ),
            RemoteButton(
                icon = Icons.Default.Circle,
                iconLabel = stringResource(R.string.record),
                onClick = remoteViewModel::record
            ),
            RemoteButton(
                icon = Icons.Default.Stop,
                iconLabel = stringResource(R.string.stop),
                onClick = remoteViewModel::stop
            ),
            RemoteButton(
                text = "RADIO",
                onClick = remoteViewModel::radio
            ),
        )
    )

    controlButtons.forEach { row ->
        Row(Modifier.widthIn(0.dp, 450.dp)) {
            row.forEach { button ->
                FilledTonalButton(
                    onClick = {
                        button.onClick()
                        performHaptic()
                    },
                    contentPadding = PaddingValues(),
                    enabled = enabled,
                    modifier =
                        Modifier
                            .padding(8.dp)
                            .weight(1f)
                            .aspectRatio(1.5f),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    button.icon?.let { icon ->
                        Icon(
                            icon,
                            contentDescription = button.iconLabel
                        )
                    }
                    button.text?.let { text ->
                        Text(text, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}