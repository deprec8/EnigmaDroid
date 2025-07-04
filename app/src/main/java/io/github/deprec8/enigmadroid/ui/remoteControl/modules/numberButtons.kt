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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.model.RemoteControlButton
import io.github.deprec8.enigmadroid.ui.remoteControl.RemoteControlViewModel

@Composable
fun NumberButtons(remoteControlViewModel: RemoteControlViewModel, enabled: Boolean, performHaptic: () -> Unit) {

    val numberButtons = listOf(
        listOf(
            RemoteControlButton(
                text = "1",
                onClick = { remoteControlViewModel.number(1) }
            ),
            RemoteControlButton(
                text = "4 ghi",
                onClick = { remoteControlViewModel.number(4) }
            ),
            RemoteControlButton(
                text = "7 pqrs",
                onClick = { remoteControlViewModel.number(7) }
            ),
        ), listOf(
            RemoteControlButton(
                text = "2 abc",
                onClick = { remoteControlViewModel.number(2) }
            ),
            RemoteControlButton(
                text = "5 jkl",
                onClick = { remoteControlViewModel.number(5) }
            ),
            RemoteControlButton(
                text = "8 tuv",
                onClick = { remoteControlViewModel.number(8) }
            ),
            RemoteControlButton(
                text = "0",
                onClick = { remoteControlViewModel.number(0) }
            ),
        ), listOf(
            RemoteControlButton(
                text = "3 def",
                onClick = { remoteControlViewModel.number(3) }
            ),
            RemoteControlButton(
                text = "6 mno",
                onClick = { remoteControlViewModel.number(6) }
            ),
            RemoteControlButton(
                text = "9 wxyz",
                onClick = { remoteControlViewModel.number(9) }
            ),
        )
    )

    Row(Modifier.widthIn(0.dp, 450.dp)) {
        numberButtons.forEach { column ->
            Column(Modifier.weight(1f)) {
                column.forEach { button ->
                    FilledTonalButton(
                        onClick = {
                            button.onClick()
                            performHaptic()
                        },
                        contentPadding = PaddingValues(),
                        enabled = enabled,
                        modifier = Modifier
                            .padding(8.dp)
                            .aspectRatio(2f),
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Text(text = button.text !!, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}