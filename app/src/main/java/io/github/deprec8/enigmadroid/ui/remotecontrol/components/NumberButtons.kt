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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.data.constants.RemoteControl
import io.github.deprec8.enigmadroid.model.RemoteControlButtonData

@Composable
fun NumberButtons(
    onKeyClicked: (RemoteControl) -> Unit
) {
    Row(Modifier.widthIn(0.dp, 500.dp)) {
        numberButtons.forEach { column ->
            Column(Modifier.weight(1f)) {
                column.forEach { button ->
                    RemoteControlButton(
                        button = button,
                        onClick = { onKeyClicked(button.key) },
                    )
                }
            }
        }
    }
}

private val numberButtons = listOf(
    listOf(
        RemoteControlButtonData(
            text = "1", key = RemoteControl.One
        ), RemoteControlButtonData(
            text = "4 ghi", key = RemoteControl.Four
        ), RemoteControlButtonData(
            text = "7 pqrs", key = RemoteControl.Seven
        )
    ), listOf(
        RemoteControlButtonData(
            text = "2 abc", key = RemoteControl.Two
        ),
        RemoteControlButtonData(
            text = "5 jkl", key = RemoteControl.Five
        ),
        RemoteControlButtonData(
            text = "8 tuv", key = RemoteControl.Eight
        ),
        RemoteControlButtonData(
            text = "0", key = RemoteControl.Zero
        ),
    ), listOf(
        RemoteControlButtonData(
            text = "3 def", key = RemoteControl.Three
        ),
        RemoteControlButtonData(
            text = "6 mno", key = RemoteControl.Six
        ),
        RemoteControlButtonData(
            text = "9 wxyz", key = RemoteControl.Nine
        ),
    )
)