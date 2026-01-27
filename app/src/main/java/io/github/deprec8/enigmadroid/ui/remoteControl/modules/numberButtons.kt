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
import io.github.deprec8.enigmadroid.data.enums.RemoteControlButtonType
import io.github.deprec8.enigmadroid.model.RemoteControlButton

@Composable
fun NumberButtons(
    onButtonClicked: (RemoteControlButtonType) -> Unit, enabled: Boolean
) {

    val numberButtons = listOf(
        listOf(
            RemoteControlButton(
                text = "1", type = RemoteControlButtonType.ONE
            ), RemoteControlButton(
                text = "4 ghi", type = RemoteControlButtonType.FOUR
            ), RemoteControlButton(
                text = "7 pqrs", type = RemoteControlButtonType.SEVEN
            )
        ), listOf(
            RemoteControlButton(
                text = "2 abc", type = RemoteControlButtonType.TWO
            ),
            RemoteControlButton(
                text = "5 jkl", type = RemoteControlButtonType.FIVE
            ),
            RemoteControlButton(
                text = "8 tuv", type = RemoteControlButtonType.EIGHT
            ),
            RemoteControlButton(
                text = "0", type = RemoteControlButtonType.ZERO
            ),
        ), listOf(
            RemoteControlButton(
                text = "3 def", type = RemoteControlButtonType.THREE
            ),
            RemoteControlButton(
                text = "6 mno", type = RemoteControlButtonType.SIX
            ),
            RemoteControlButton(
                text = "9 wxyz", type = RemoteControlButtonType.NINE
            ),
        )
    )

    Row(Modifier.widthIn(0.dp, 500.dp)) {
        numberButtons.forEach { column ->
            Column(Modifier.weight(1f)) {
                column.forEach { button ->
                    FilledTonalButton(
                        onClick = {
                            onButtonClicked(button.type)
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