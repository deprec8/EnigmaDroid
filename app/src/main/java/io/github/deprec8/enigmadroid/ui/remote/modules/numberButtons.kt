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
import io.github.deprec8.enigmadroid.model.RemoteButton
import io.github.deprec8.enigmadroid.ui.remote.RemoteViewModel

@Composable
fun NumberButtons(remoteViewModel: RemoteViewModel, enabled: Boolean, performHaptic: () -> Unit) {

    val numberButtons = listOf(
        listOf(
            RemoteButton(
                text = "1",
                onClick = { remoteViewModel.number(1) }
            ),
            RemoteButton(
                text = "4 ghi",
                onClick = { remoteViewModel.number(4) }
            ),
            RemoteButton(
                text = "7 pqrs",
                onClick = { remoteViewModel.number(7) }
            ),
        ), listOf(
            RemoteButton(
                text = "2 abc",
                onClick = { remoteViewModel.number(2) }
            ),
            RemoteButton(
                text = "5 jkl",
                onClick = { remoteViewModel.number(5) }
            ),
            RemoteButton(
                text = "8 tuv",
                onClick = { remoteViewModel.number(8) }
            ),
            RemoteButton(
                text = "0",
                onClick = { remoteViewModel.number(0) }
            ),
        ), listOf(
            RemoteButton(
                text = "3 def",
                onClick = { remoteViewModel.number(3) }
            ),
            RemoteButton(
                text = "6 mno",
                onClick = { remoteViewModel.number(6) }
            ),
            RemoteButton(
                text = "9 wxyz",
                onClick = { remoteViewModel.number(9) }
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