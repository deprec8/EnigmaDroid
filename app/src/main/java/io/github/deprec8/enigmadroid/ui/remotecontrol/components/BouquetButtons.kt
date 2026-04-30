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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.RemoteControlKey
import io.github.deprec8.enigmadroid.model.RemoteControlButtonData

@Composable
fun BouquetButtons(
    onButtonClicked: (RemoteControlKey) -> Unit, enabled: Boolean
) {
    Row(
        Modifier.widthIn(0.dp, 500.dp)
    ) {
        bouquetButtons.forEach { button ->
            RemoteControlButton(
                button = button,
                onClick = { onButtonClicked(button.key) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                aspectRatio = 1.5f
            )
        }
    }
}

private val bouquetButtons = listOf(
    RemoteControlButtonData(
        icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        iconLabelRes = R.string.bouquet_down,
        key = RemoteControlKey.PreviousBouquet
    ),
    RemoteControlButtonData(
        text = "INFO", key = RemoteControlKey.Info
    ),
    RemoteControlButtonData(
        text = "TEXT", key = RemoteControlKey.Text
    ),
    RemoteControlButtonData(
        icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        iconLabelRes = R.string.bouquet_up,
        key = RemoteControlKey.NextBouquet
    ),
)