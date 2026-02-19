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

package io.github.deprec8.enigmadroid.ui.stream.movie.components

import androidx.annotation.OptIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlaybackSpeedState
import io.github.deprec8.enigmadroid.R

@OptIn(UnstableApi::class)
@Composable
fun PlaybackSpeedButton(player: Player, onBlockUiHiding: (isBlocking: Boolean) -> Unit) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    val playbackSpeedState = rememberPlaybackSpeedState(player)
    val scrollState = rememberScrollState()

    val playbackSpeeds = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.5f, 2.0f, 3.0f, 4.0f)

    TextButton(onClick = {
        showMenu = true
        onBlockUiHiding(true)
    }, colors = ButtonDefaults.textButtonColors(contentColor = Color.White)) {
        Text(playbackSpeedState.playbackSpeed.toString() + "x")
        DropdownMenu(
            expanded = showMenu, onDismissRequest = {
                showMenu = false
                onBlockUiHiding(false)
            }, scrollState = scrollState
        ) {
            playbackSpeeds.forEach {
                DropdownMenuItem(text = { Text(it.toString() + "x") }, onClick = {
                    player.setPlaybackSpeed(it)
                    showMenu = false
                    onBlockUiHiding(false)
                }, leadingIcon = {
                    if (playbackSpeedState.playbackSpeed == it) {
                        Icon(Icons.Default.Check, stringResource(R.string.current_playback_speed))
                    }
                })
            }

        }
    }
}