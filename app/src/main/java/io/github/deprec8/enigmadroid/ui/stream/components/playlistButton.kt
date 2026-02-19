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

package io.github.deprec8.enigmadroid.ui.stream.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FeaturedPlayList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.stream.components.states.rememberPlaylistState

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistButton(player: Player, onBlockUiHiding: (isBlocking: Boolean) -> Unit) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val playlistState = rememberPlaylistState(player)

    IconButton(
        onClick = {
            showDialog = true
            onBlockUiHiding(true)
        }) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.FeaturedPlayList,
            contentDescription = stringResource(R.string.show_playlist),
            tint = Color.White
        )
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = {
            showDialog = false
            onBlockUiHiding(false)
        }, title = {
            Text(stringResource(R.string.playlist))
        }, text = {
            LazyColumn {
                itemsIndexed(playlistState.items) { index, item ->
                    ListItem(
                        headlineContent = {
                            Text(item.mediaMetadata.title.toString())
                        }, supportingContent = {
                            Text(item.mediaMetadata.artist.toString())
                        }, leadingContent = {
                            AnimatedVisibility(
                                visible = playlistState.currentIndex == index,
                                enter = expandIn(expandFrom = Alignment.Center) + fadeIn(),
                                exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    stringResource(R.string.current_media_item)
                                )
                            }
                        }, modifier = Modifier.clickable {
                            if (playlistState.currentIndex != index) {
                                playlistState.seekToIndex(index)
                            }
                        }, colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }, confirmButton = {
            TextButton(onClick = {
                showDialog = false
                onBlockUiHiding(false)
            }) {
                Text(stringResource(R.string.close))
            }
        }, modifier = Modifier.padding(16.dp))
    }
}