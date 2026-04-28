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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.common.enums.RCButton
import io.github.deprec8.enigmadroid.model.RemoteControlButton

@Composable
fun NumberButtons(
    onButtonClicked: (RCButton) -> Unit, enabled: Boolean
) {

    val numberButtons = listOf(
        listOf(
            RemoteControlButton(
                text = "1", button = RCButton.ONE
            ), RemoteControlButton(
                text = "4 ghi", button = RCButton.FOUR
            ), RemoteControlButton(
                text = "7 pqrs", button = RCButton.SEVEN
            )
        ), listOf(
            RemoteControlButton(
                text = "2 abc", button = RCButton.TWO
            ),
            RemoteControlButton(
                text = "5 jkl", button = RCButton.FIVE
            ),
            RemoteControlButton(
                text = "8 tuv", button = RCButton.EIGHT
            ),
            RemoteControlButton(
                text = "0", button = RCButton.ZERO
            ),
        ), listOf(
            RemoteControlButton(
                text = "3 def", button = RCButton.THREE
            ),
            RemoteControlButton(
                text = "6 mno", button = RCButton.SIX
            ),
            RemoteControlButton(
                text = "9 wxyz", button = RCButton.NINE
            ),
        )
    )

    Row(Modifier.widthIn(0.dp, 500.dp)) {
        numberButtons.forEach { column ->
            Column(Modifier.weight(1f)) {
                column.forEach { button ->
                    RemoteControlButton(
                        button = button,
                        onClick = { onButtonClicked(button.button) },
                        enabled = enabled
                    )
                }
            }
        }
    }
}