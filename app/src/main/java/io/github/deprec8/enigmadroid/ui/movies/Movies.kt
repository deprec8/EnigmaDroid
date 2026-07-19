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

package io.github.deprec8.enigmadroid.ui.movies

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.ui.components.ConnectionDisplay
import io.github.deprec8.enigmadroid.ui.components.FloatingReloadButton
import io.github.deprec8.enigmadroid.ui.components.ObserveActiveState
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.navigation.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.movies.components.MoviesActionBar
import io.github.deprec8.enigmadroid.ui.movies.components.MoviesContent
import io.github.deprec8.enigmadroid.utils.IntentUtils
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesPage(
    onNavigateToRemoteControl: () -> Unit,
    onNavigateToDirectory: (String) -> Unit,
    drawerState: DrawerState,
    moviesViewModel: MoviesViewModel = koinViewModel()
) {

    val movieBatch by moviesViewModel.movieBatch.collectAsStateWithLifecycle()
    val filteredMovies by moviesViewModel.filteredMovies.collectAsStateWithLifecycle()
    val searchHistory by moviesViewModel.searchHistory.collectAsStateWithLifecycle()
    val connectionState by moviesViewModel.connectionState.collectAsStateWithLifecycle()
    val highlightedWords by moviesViewModel.highlightedWords.collectAsStateWithLifecycle()
    val freeSpace by moviesViewModel.freeSpace.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveActiveState(moviesViewModel)

    Scaffold(floatingActionButton = {
        FloatingReloadButton(connectionState) { moviesViewModel.fetchData() }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = movieBatch?.movies?.isNotEmpty() == true && connectionState == ConnectionState.CONNECTED,
            textFieldState = moviesViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_movies),
            content = {
                filteredMovies?.let {
                    MoviesContent(
                        movies = it,
                        paddingValues = PaddingValues(0.dp),
                        highlightedWords = highlightedWords,
                        onStreamMovie = { movie ->
                            scope.launch {
                                IntentUtils.playMedia(
                                    context,
                                    moviesViewModel.buildMovieStreamUrl(movie.fileName),
                                    movie.eventName
                                )
                            }
                        },
                        onPlayMovieOnDevice = { movie -> moviesViewModel.playOnDevice(movie.serviceReference) },
                        onDeleteMovie = { movie -> moviesViewModel.delete(movie.serviceReference) },
                        onRenameMovie = { movie, newName ->
                            moviesViewModel.rename(
                                movie.serviceReference, newName
                            )
                        },
                        onMoveMovie = { movie, newLocation ->
                            moviesViewModel.move(
                                movie.serviceReference, newLocation
                            )
                        },
                        onDownloadMovie = { movie -> moviesViewModel.download(movie) })

                } ?: run {
                    SearchHistory(searchHistory = searchHistory, onTermSearchClick = {
                        moviesViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                        moviesViewModel.updateSearchInput()
                    }, onTermInsertClick = {
                        moviesViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
                            it
                        )
                    })
                }
            },
            navigationButton = { searchBarState ->
                SearchTopAppBarDrawerNavigationButton(drawerState, searchBarState)
            },
            actionButtons = {
                RemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
            },
            onSearch = {
                moviesViewModel.updateSearchInput()
            },
            actionBar = {
                MoviesActionBar(movieBatch, freeSpace, connectionState)
            })
    }

    ) { innerPadding ->
        if (movieBatch != null && connectionState == ConnectionState.CONNECTED) {
            MoviesContent(
                movies = movieBatch?.movies ?: emptyList(),
                bookmarks = movieBatch?.bookmarks ?: emptyList(),
                directory = movieBatch?.directory ?: "",
                paddingValues = innerPadding,
                onStreamMovie = { movie ->
                    scope.launch {
                        IntentUtils.playMedia(
                            context,
                            moviesViewModel.buildMovieStreamUrl(movie.fileName),
                            movie.eventName
                        )
                    }
                },
                onPlayMovieOnDevice = { movie -> moviesViewModel.playOnDevice(movie.serviceReference) },
                onDeleteMovie = { movie -> moviesViewModel.delete(movie.serviceReference) },
                onRenameMovie = { movie, newName ->
                    moviesViewModel.rename(
                        movie.serviceReference, newName
                    )
                },
                onMoveMovie = { movie, newLocation ->
                    moviesViewModel.move(
                        movie.serviceReference, newLocation
                    )
                },
                onDownloadMovie = { movie -> moviesViewModel.download(movie) },
                onNavigateToDirectory = { path ->
                    onNavigateToDirectory(
                        path
                    )
                })
        } else {
            ConnectionDisplay(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                onCheckConnection = {
                    moviesViewModel.checkConnection()
                },
                connectionState = connectionState
            )
        }
    }
}