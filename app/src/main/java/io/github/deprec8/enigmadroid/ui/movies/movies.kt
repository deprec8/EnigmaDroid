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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.ui.components.FloatingRefreshButton
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.movies.components.MoviesContent
import io.github.deprec8.enigmadroid.utils.IntentUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesPage(
    onNavigateToRemoteControl: () -> Unit,
    drawerState: DrawerState,
    moviesViewModel: MoviesViewModel = hiltViewModel()
) {

    val movieBatches by moviesViewModel.movieBatches.collectAsStateWithLifecycle()
    val filteredMovies by moviesViewModel.filteredMovies.collectAsStateWithLifecycle()
    val searchHistory by moviesViewModel.searchHistory.collectAsStateWithLifecycle()
    val loadingState by moviesViewModel.loadingState.collectAsStateWithLifecycle()
    val searchInput by moviesViewModel.searchInput.collectAsStateWithLifecycle()
    val useSearchHighlighting by moviesViewModel.useSearchHighlighting.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { movieBatches.size })
    val selectedTabIndex = remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0, (if (movieBatches.size - 1 < 0) {
                    0
                } else {
                    movieBatches.size - 1
                })
            )
        }
    }
    val highlightedWords = remember(searchInput) {
        searchInput.split(" ").filter { it.isNotBlank() }
    }

    LaunchedEffect(Unit) {
        moviesViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            moviesViewModel.fetchData()
        }
    }

    Scaffold(floatingActionButton = {
        FloatingRefreshButton(loadingState) { moviesViewModel.fetchData() }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = movieBatches.isNotEmpty() && loadingState == LoadingState.LOADED,
            textFieldState = moviesViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_movies),
            content = {
                if (filteredMovies != null) {
                    MoviesContent(
                        movies = filteredMovies !!,
                        paddingValues = PaddingValues(0.dp),
                        highlightedWords = if (useSearchHighlighting) {
                            highlightedWords
                        } else {
                            emptyList()
                        },
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
                        onDownloadMovie = { movie -> moviesViewModel.downloadMovie(movie) })
                } else {
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
            tabBar = {
                if (movieBatches.isNotEmpty() && loadingState == LoadingState.LOADED) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex.value,
                        divider = { },
                        scrollState = rememberScrollState()
                    ) {
                        movieBatches.forEachIndexed { index, movieBatch ->
                            Tab(
                                text = {
                                    Text(
                                        text = movieBatch.bookmark.displayName,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                },
                                selected = index == selectedTabIndex.value,
                            )
                        }
                    }
                    HorizontalDivider()

                }
            })

    }

    ) { innerPadding ->
        if (movieBatches.isNotEmpty() && loadingState == LoadingState.LOADED) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { index ->
                MoviesContent(
                    movies = movieBatches[index].movies,
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
                    onDownloadMovie = { movie -> moviesViewModel.downloadMovie(movie) })
            }
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
                }, loadingState = loadingState
            )

        }
    }
}