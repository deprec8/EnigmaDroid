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

package io.github.deprec8.enigmadroid.ui.movies.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.automirrored.outlined.DriveFileMove
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.api.movies.Movie
import io.github.deprec8.enigmadroid.model.menu.MenuItem
import io.github.deprec8.enigmadroid.model.menu.MenuItemGroup
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.content.ContentListItem

@Composable
fun MoviesContent(
    movies: List<Movie>,
    paddingValues: PaddingValues,
    highlightedWords: List<String> = emptyList(),
    onStreamMovie: (Movie) -> Unit,
    onPlayMovieOnDevice: (Movie) -> Unit,
    onDeleteMovie: (Movie) -> Unit,
    onRenameMovie: (Movie, String) -> Unit,
    onMoveMovie: (Movie, String) -> Unit,
    onDownloadMovie: (Movie) -> Unit
) {
    if (movies.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(310.dp),
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .imePadding(),
            contentPadding = paddingValues
        ) {
            items(movies) { movie ->
                var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
                var showRenameDialog by rememberSaveable { mutableStateOf(false) }
                var showMoveDialog by rememberSaveable { mutableStateOf(false) }

                ContentListItem(
                    highlightedWords = highlightedWords,
                    headlineText = movie.eventName,
                    overlineText = movie.serviceName.ifBlank {
                        null
                    },
                    supportingText = "${movie.begin} / ${movie.length} / ${movie.filesizeReadable}",
                    shortDescription = movie.shortDescription,
                    longDescription = movie.longDescription,
                    menuItemGroups = listOf(
                        MenuItemGroup(
                            listOf(
                                MenuItem(
                                    text = stringResource(R.string.stream),
                                    outlinedIcon = Icons.Outlined.Cast,
                                    filledIcon = Icons.Filled.Cast,
                                    action = {
                                        onStreamMovie(movie)
                                    }), MenuItem(
                                    text = stringResource(R.string.switch_channel),
                                    outlinedIcon = Icons.Outlined.PlayArrow,
                                    filledIcon = Icons.Filled.PlayArrow,
                                    action = {
                                        onPlayMovieOnDevice(movie)
                                    })
                            )
                        ),
                        MenuItemGroup(
                            listOf(
                                MenuItem(
                                    text = stringResource(R.string.download),
                                    outlinedIcon = Icons.Outlined.Download,
                                    filledIcon = Icons.Filled.Download,
                                    action = {
                                        onDownloadMovie(movie)
                                    })
                            )
                        ),
                    ),
                    editMenuItemGroup = MenuItemGroup(
                        listOf(
                            MenuItem(
                                text = stringResource(R.string.rename),
                                outlinedIcon = Icons.Outlined.Edit,
                                filledIcon = Icons.Filled.Edit,
                                action = { showRenameDialog = true }), MenuItem(
                                text = stringResource(R.string.move),
                                outlinedIcon = Icons.AutoMirrored.Outlined.DriveFileMove,
                                filledIcon = Icons.AutoMirrored.Filled.DriveFileMove,
                                action = { showMoveDialog = true }), MenuItem(
                                text = stringResource(R.string.delete),
                                outlinedIcon = Icons.Outlined.Delete,
                                filledIcon = Icons.Filled.Delete,
                                action = { showDeleteDialog = true })
                        )
                    )
                )

                if (showDeleteDialog) {
                    DeleteMovieDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        onConfirmRequest = {
                            showDeleteDialog = false
                            onDeleteMovie(movie)
                        })
                }

                if (showRenameDialog) {
                    RenameMovieDialog(
                        movie,
                        onDismissRequest = { showRenameDialog = false },
                        onConfirmRequest = {
                            showRenameDialog = false
                            onRenameMovie(movie, it)
                        })
                }

                if (showMoveDialog) {
                    MoveMovieDialog(
                        onDismissRequest = { showMoveDialog = false },
                        onConfirmRequest = {
                            showMoveDialog = false
                            onMoveMovie(movie, it)
                        })
                }
            }
        }
    } else {
        NoResults(
            Modifier
                .consumeWindowInsets(paddingValues)
                .padding(paddingValues)
        )
    }
}