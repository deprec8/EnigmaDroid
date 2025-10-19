/*
 * Copyright (C) 2025 deprec8
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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.automirrored.outlined.DriveFileMove
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.Movie
import io.github.deprec8.enigmadroid.model.menu.MenuItem
import io.github.deprec8.enigmadroid.model.menu.MenuSection
import io.github.deprec8.enigmadroid.ui.components.ContentListItem
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.calculateSearchTopAppBarContentPaddingValues
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.utils.IntentUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesPage(
    onNavigateToRemoteControl: () -> Unit,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState, moviesViewModel: MoviesViewModel = hiltViewModel()
) {

    val movies by moviesViewModel.movies.collectAsStateWithLifecycle()
    val filteredMovies by moviesViewModel.filteredMovies.collectAsStateWithLifecycle()
    val searchHistory by moviesViewModel.searchHistory.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { movies.size })
    val selectedTabIndex = remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0,
                (if (movies.size - 1 < 0) {
                    0
                } else {
                    movies.size - 1
                })
            )
        }
    }
    val loadingState by moviesViewModel.loadingState.collectAsStateWithLifecycle()
    val searchInput by moviesViewModel.searchInput.collectAsStateWithLifecycle()
    val useSearchHighlighting by moviesViewModel.useSearchHighlighting.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        moviesViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            moviesViewModel.fetchData()
        }
    }

    @Composable
    fun Content(
        list: List<Movie>,
        paddingValues: PaddingValues,
        highlightedWords: List<String> = emptyList()
    ) {
        if (list.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(310.dp),
                Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues)
                    .imePadding(),
                contentPadding = paddingValues
            ) {
                items(list) { movie ->
                    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
                    var showRenameDialog by rememberSaveable { mutableStateOf(false) }
                    var showMoveDialog by rememberSaveable { mutableStateOf(false) }

                    ContentListItem(
                        highlightedWords = highlightedWords,
                        headlineText = movie.eventName,
                        overlineText = if (movie.serviceName != "") {
                            movie.serviceName
                        } else {
                            null
                        },
                        supportingText = movie.begin + " / " + movie.length + " / " + movie.filesizeReadable,
                        shortDescription = movie.shortDescription,
                        longDescription = movie.longDescription,
                        menuSections = listOf(
                            MenuSection(
                                listOf(
                                    MenuItem(
                                        text = stringResource(R.string.stream),
                                        outlinedIcon = Icons.Outlined.Cast,
                                        filledIcon = Icons.Filled.Cast,
                                        action = {
                                            scope.launch {
                                                IntentUtils.playMedia(
                                                    context,
                                                    moviesViewModel.buildStreamUrl(movie.fileName),
                                                    movie.eventName
                                                )
                                            }
                                        }
                                    ),
                                    MenuItem(
                                        text = stringResource(R.string.switch_channel),
                                        outlinedIcon = Icons.Outlined.PlayArrow,
                                        filledIcon = Icons.Filled.PlayArrow,
                                        action = {
                                            moviesViewModel.play(movie.serviceReference)
                                        }
                                    )
                                )
                            ),
                            MenuSection(
                                listOf(
                                    MenuItem(
                                        text = stringResource(R.string.download),
                                        outlinedIcon = Icons.Outlined.Download,
                                        filledIcon = Icons.Filled.Download,
                                        action = {
                                            moviesViewModel.downloadMovie(movie)
                                        }
                                    ))
                            ),
                        ),
                        editMenuSection = MenuSection(
                            listOf(
                                MenuItem(
                                    text = stringResource(R.string.rename),
                                    outlinedIcon = Icons.Outlined.Edit,
                                    filledIcon = Icons.Filled.Edit,
                                    action = { showRenameDialog = true }
                                ),
                                MenuItem(
                                    text = stringResource(R.string.move),
                                    outlinedIcon = Icons.AutoMirrored.Outlined.DriveFileMove,
                                    filledIcon = Icons.AutoMirrored.Filled.DriveFileMove,
                                    action = { showMoveDialog = true }
                                ),
                                MenuItem(
                                    text = stringResource(R.string.delete),
                                    outlinedIcon = Icons.Outlined.Delete,
                                    filledIcon = Icons.Filled.Delete,
                                    action = { showDeleteDialog = true }
                                )
                            )
                        )
                    )

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDeleteDialog = false
                            },
                            title = { Text(text = stringResource(R.string.delete_movie)) },
                            text = { Text(text = stringResource(R.string.if_you_delete_this_movie_it_will_not_be_recoverable)) },
                            icon = {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = null
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    showDeleteDialog = false
                                    moviesViewModel.delete(movie.serviceReference)
                                }) { Text(stringResource(R.string.confirm)) }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDeleteDialog = false
                                }) { Text(stringResource(R.string.cancel)) }
                            }
                        )
                    }

                    if (showRenameDialog) {
                        var renameInput by rememberSaveable {
                            mutableStateOf(movie.eventName)
                        }
                        AlertDialog(
                            onDismissRequest = {
                                showRenameDialog = false
                            },
                            title = { Text(text = stringResource(R.string.rename_movie)) },
                            icon = {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = null
                                )
                            },
                            text = {
                                OutlinedTextField(
                                    value = renameInput,
                                    onValueChange = { renameInput = it }, label = {
                                        Text(
                                            text = stringResource(R.string.new_name)
                                        )
                                    })
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showRenameDialog = false
                                        moviesViewModel.rename(
                                            movie.serviceReference,
                                            renameInput
                                        )
                                    },
                                    enabled = renameInput != movie.eventName && ! renameInput.isBlank()
                                ) { Text(stringResource(R.string.confirm)) }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showRenameDialog = false
                                }) { Text(stringResource(R.string.cancel)) }
                            }
                        )
                    }

                    if (showMoveDialog) {
                        var moveInput by rememberSaveable {
                            mutableStateOf("")
                        }
                        AlertDialog(
                            onDismissRequest = {
                                showMoveDialog = false
                            },
                            title = { Text(text = stringResource(R.string.move_movie)) },
                            text = {
                                OutlinedTextField(
                                    prefix = { Text("/") },
                                    value = moveInput,
                                    onValueChange = { moveInput = it }, label = {
                                        Text(
                                            text = stringResource(R.string.new_location)
                                        )
                                    })
                            },
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Outlined.DriveFileMove,
                                    contentDescription = null
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    showMoveDialog = false
                                    moviesViewModel.move(movie.serviceReference, moveInput)
                                }) { Text(stringResource(R.string.confirm)) }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showMoveDialog = false
                                }) { Text(stringResource(R.string.cancel)) }
                            }
                        )
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

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                loadingState == LoadingState.LOADED,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    moviesViewModel.fetchData()
                }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh_page)
                    )
                }
            }
        },
        contentWindowInsets = contentWithDrawerWindowInsets(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }, topBar = {
            SearchTopAppBar(
                enabled = movies.isNotEmpty(),
                textFieldState = moviesViewModel.searchFieldState,
                placeholder = stringResource(R.string.search_movies),
                content = {
                    if (filteredMovies != null) {
                        Content(
                            list = filteredMovies !!,
                            paddingValues = calculateSearchTopAppBarContentPaddingValues(),
                            highlightedWords = if (useSearchHighlighting) searchInput.split(" ")
                                .filter { it.isNotBlank() } else emptyList()
                        )
                    } else {
                        SearchHistory(
                            searchHistory = searchHistory,
                            onTermSearchClick = {
                                moviesViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                                moviesViewModel.updateSearchInput()
                            },
                            onTermInsertClick = {
                                moviesViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
                                    it
                                )
                            }
                        )
                    }
                },
                drawerState = drawerState,
                onNavigateToRemote = { onNavigateToRemoteControl() },
                onSearch = {
                    moviesViewModel.updateSearchInput()
                },
                tabBar = {
                    if (movies.isNotEmpty()) {
                        PrimaryScrollableTabRow(
                            selectedTabIndex = selectedTabIndex.value,
                            divider = { },
                            scrollState = rememberScrollState()
                        ) {
                            movies.forEachIndexed { index, movieList ->
                                Tab(
                                    text = {
                                        Text(
                                            text = movieList.bookmark.displayName,
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
                }
            )

        }

    ) { innerPadding ->
        if (movies.isNotEmpty()) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { index ->
                Content(list = movies[index].movies, innerPadding)
            }
        } else {
            LoadingScreen(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),

                updateLoadingState = {
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