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

package io.github.deprec8.enigmadroid.ui.stream.movie.components.states

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.observeState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun rememberProgressState(player: Player): ProgressState {
    val progressState = remember(player) { ProgressState(player) }
    LaunchedEffect(player) { progressState.observe() }
    return progressState
}

@OptIn(UnstableApi::class)
class ProgressState(
    private val player: Player
) {

    var position by mutableLongStateOf(0L)
        private set

    var duration by mutableLongStateOf(0L)
        private set

    var isSeeking by mutableStateOf(false)
        private set

    private val playerStateObserver = player.observeState(
        Player.EVENT_PLAYBACK_STATE_CHANGED,
        Player.EVENT_POSITION_DISCONTINUITY,
        Player.EVENT_TIMELINE_CHANGED,
        Player.EVENT_IS_PLAYING_CHANGED
    ) {
        if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            duration = player.duration.coerceAtLeast(0L)

            if (! isSeeking) {
                position = player.currentPosition
            }
        }
    }

    fun onSeekStarted() {
        isSeeking = true
    }

    fun onSeekChanged(newPosition: Long) {
        position = newPosition
    }

    fun onSeekFinished() {
        player.seekTo(position)
        isSeeking = false
    }

    suspend fun observe() {
        coroutineScope {
            launch {
                playerStateObserver.observe()
            }

            launch {
                while (true) {
                    if (player.isPlaying && ! isSeeking) {
                        position = player.currentPosition
                    }
                    delay(1000)
                }
            }
        }
    }
}
