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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.remoteControl.RemoteControlViewModel

@Composable
fun MediaButtons(remoteControlViewModel: RemoteControlViewModel, enabled: Boolean, performHaptic: () -> Unit) {

    Row(
        Modifier
            .widthIn(0.dp, 450.dp)
            .height(IntrinsicSize.Max)
    ) {
        Card(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = if (! enabled) {
                    ButtonDefaults.buttonColors().disabledContainerColor
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            )
        ) {
            OutlinedButton(
                onClick = {
                    remoteControlViewModel.volUP()
                    performHaptic()
                },
                shape = MaterialTheme.shapes.extraLarge,
                contentPadding = PaddingValues(),
                enabled = enabled,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .fillMaxSize()
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.volume_up)
                )
            }
            Text(
                text = "VOL", textAlign = TextAlign.Center,
                color = if (! enabled) {
                    ButtonDefaults.buttonColors().disabledContentColor
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            )
            OutlinedButton(
                onClick = {
                    remoteControlViewModel.volDown()
                    performHaptic()
                },
                shape = MaterialTheme.shapes.extraLarge,
                contentPadding = PaddingValues(),
                enabled = enabled,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .fillMaxSize()
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = stringResource(R.string.volume_down)
                )
            }
        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            FilledTonalButton(
                onClick = {
                    remoteControlViewModel.audio()
                    performHaptic()
                },
                shape = MaterialTheme.shapes.extraLarge,
                contentPadding = PaddingValues(),
                enabled = enabled,
                modifier =
                    Modifier
                        .padding(8.dp)
                        .weight(1f)

                        .aspectRatio(2f)
            ) {
                Text(text = "AUDIO", textAlign = TextAlign.Center)
            }
            FilledTonalButton(
                onClick = {
                    remoteControlViewModel.volMute()
                    performHaptic()
                },
                shape = MaterialTheme.shapes.extraLarge,
                contentPadding = PaddingValues(),
                enabled = enabled,
                modifier =
                    Modifier
                        .padding(8.dp)
                        .weight(1f)
                        .aspectRatio(2f)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = stringResource(R.string.mute)
                )
            }
            FilledTonalButton(
                onClick = {
                    remoteControlViewModel.help()
                    performHaptic()
                },
                shape = MaterialTheme.shapes.extraLarge,
                contentPadding = PaddingValues(),
                enabled = enabled,
                modifier =
                    Modifier
                        .padding(8.dp)
                        .weight(1f)

                        .aspectRatio(2f)
            ) {
                Text(text = "HELP", textAlign = TextAlign.Center)
            }
        }
        Card(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = if (! enabled) {
                    ButtonDefaults.buttonColors().disabledContainerColor
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            )
        ) {
            OutlinedButton(
                onClick = {
                    remoteControlViewModel.chUP()
                    performHaptic()
                },
                contentPadding = PaddingValues(),
                enabled = enabled,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .fillMaxSize(),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.channel_up)
                )
            }
            Text(
                text = "CH", textAlign = TextAlign.Center,
                color = if (! enabled) {
                    ButtonDefaults.buttonColors().disabledContentColor
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            )
            OutlinedButton(
                onClick = {
                    remoteControlViewModel.chDown()
                    performHaptic()
                },
                contentPadding = PaddingValues(),
                enabled = enabled,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .fillMaxSize(),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.channel_down)
                )
            }
        }
    }
}