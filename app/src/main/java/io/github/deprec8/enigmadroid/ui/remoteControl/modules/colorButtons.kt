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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.RemoteControlButtonType
import io.github.deprec8.enigmadroid.model.RemoteControlButton

@Composable
fun ColorButtons(onButtonClicked: (RemoteControlButtonType) -> Unit, enabled: Boolean) {

    val colorButtons = listOf(
        RemoteControlButton(
            icon = Icons.Default.TripOrigin,
            iconLabel = stringResource(R.string.red),
            iconTint = Color.Red,
            type = RemoteControlButtonType.COLOR_RED
        ),
        RemoteControlButton(
            icon = Icons.Default.TripOrigin,
            iconLabel = stringResource(R.string.green),
            iconTint = Color.Green,
            type = RemoteControlButtonType.COLOR_GREEN
        ),
        RemoteControlButton(
            icon = Icons.Default.TripOrigin,
            iconLabel = stringResource(R.string.yellow),
            iconTint = Color.Yellow,
            type = RemoteControlButtonType.COLOR_YELLOW
        ),
        RemoteControlButton(
            icon = Icons.Default.TripOrigin,
            iconLabel = stringResource(R.string.blue),
            iconTint = Color.Blue,
            type = RemoteControlButtonType.COLOR_BLUE
        ),
    )

    Row(
        Modifier.widthIn(0.dp, 500.dp)
    ) {
        colorButtons.forEach { button ->
            FilledTonalButton(
                onClick = {
                    onButtonClicked(button.type)
                },
                contentPadding = PaddingValues(),
                enabled = enabled,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
                    .aspectRatio(1.5f),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Icon(
                    button.icon !!,
                    contentDescription = button.iconLabel !!,
                    tint = if (! enabled) {
                        ButtonDefaults.buttonColors().disabledContentColor
                    } else {
                        button.iconTint !!
                    }
                )
            }
        }
    }
}