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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.movies.MovieBatch
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.loading.FloatingReloadButton
import io.github.deprec8.enigmadroid.ui.components.loading.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.navigation.ArrowNavigationButton
import io.github.deprec8.enigmadroid.ui.components.navigation.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.movies.components.MoviesActionBar
import io.github.deprec8.enigmadroid.ui.movies.components.MoviesContent
import io.github.deprec8.enigmadroid.utils.IntentUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesDirectoryPage(
    onNavigateToRemoteControl: () -> Unit,
    onNavigateToDirectory: (String, MovieBatch?) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToTop: () -> Unit,
    moviesViewModel: MoviesViewModel = hiltViewModel()
) {

    val movieBatch by moviesViewModel.movieBatch.collectAsStateWithLifecycle()
    val filteredMovies by moviesViewModel.filteredMovies.collectAsStateWithLifecycle()
    val searchHistory by moviesViewModel.searchHistory.collectAsStateWithLifecycle()
    val loadingState by moviesViewModel.loadingState.collectAsStateWithLifecycle()
    val highlightedWords by moviesViewModel.highlightedWords.collectAsStateWithLifecycle()
    val preloadBatches by moviesViewModel.preloadBatches.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        moviesViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            moviesViewModel.fetchData()
        }
    }

    Scaffold(floatingActionButton = {
        FloatingReloadButton(loadingState) { moviesViewModel.fetchData() }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = movieBatch != MovieBatch() && loadingState == LoadingState.LOADED,
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
            navigationButton = {
                ArrowNavigationButton { onNavigateBack() }
            },
            actionButtons = {
                Row {
                    TooltipBox(
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.go_top))
                            }
                        },
                        state = rememberTooltipState(),
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Below, 4.dp
                        )
                    ) {
                        IconButton(
                            onClick = { onNavigateToTop() }) {
                            Icon(Icons.Filled.ArrowUpward, stringResource(R.string.go_top))
                        }
                    }
                    RemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
                }
            },
            onSearch = {
                moviesViewModel.updateSearchInput()
            },
            actionBar = {
                MoviesActionBar(movieBatch, loadingState)
            })
    }

    ) { innerPadding ->
        if (movieBatch != null && loadingState == LoadingState.LOADED) {
            MoviesContent(
                movies = movieBatch?.movies ?: emptyList(),
                bookmarks = movieBatch?.bookmarks ?: emptyList(),
                directory = movieBatch?.directory ?: "",
                paddingValues = innerPadding,
                preloadBatches = preloadBatches,
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
                onNavigateToDirectory = { path, preloadBatch ->
                    onNavigateToDirectory(path, preloadBatch)
                })
        } else {
            LoadingScreen(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                onUpdateLoadingState = {
                    scope.launch {
                        moviesViewModel.updateLoadingState(
                            it
                        )
                    }
                },
                loadingState = loadingState
            )
        }
    }
}