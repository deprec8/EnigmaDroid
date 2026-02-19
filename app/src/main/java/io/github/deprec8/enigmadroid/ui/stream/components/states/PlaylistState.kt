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

package io.github.deprec8.enigmadroid.ui.stream.components.states

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.observeState

@OptIn(UnstableApi::class)
@Composable
fun rememberPlaylistState(player: Player): PlaylistState {
    val playlistState = remember(player) { PlaylistState(player) }
    LaunchedEffect(player) { playlistState.observe() }
    return playlistState
}

@UnstableApi
class PlaylistState(private val player: Player) {

    var items by mutableStateOf<List<MediaItem>>(emptyList())
        private set

    var currentIndex by mutableIntStateOf(0)
        private set

    private val playerStateObserver = player.observeState(
        Player.EVENT_MEDIA_ITEM_TRANSITION, Player.EVENT_TIMELINE_CHANGED
    ) {
        items = List(player.mediaItemCount) { player.getMediaItemAt(it) }
        currentIndex = player.currentMediaItemIndex
    }

    fun seekToIndex(index: Int) {
        if (index in items.indices) player.seekToDefaultPosition(index)
    }

    suspend fun observe(): Nothing = playerStateObserver.observe()
}