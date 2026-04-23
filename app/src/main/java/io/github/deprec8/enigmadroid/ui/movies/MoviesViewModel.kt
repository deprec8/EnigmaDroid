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

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.DownloadRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SearchHistoryRepository
import io.github.deprec8.enigmadroid.data.SettingsRepository
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.movies.Movie
import io.github.deprec8.enigmadroid.model.api.movies.MovieBatch
import io.github.deprec8.enigmadroid.ui.components.search.asHighlightedWords
import io.github.deprec8.enigmadroid.utils.FilterUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository,
    private val downloadRepository: DownloadRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _filteredMovies = MutableStateFlow<List<Movie>?>(null)
    val filteredMovies: StateFlow<List<Movie>?> = _filteredMovies.asStateFlow()

    private val _movieBatch = MutableStateFlow<MovieBatch?>(null)
    val movieBatch: StateFlow<MovieBatch?> = _movieBatch.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _preloadBatches = MutableStateFlow<Map<String, MovieBatch>>(emptyMap())
    val preloadBatches: StateFlow<Map<String, MovieBatch>> = _preloadBatches.asStateFlow()

    val searchFieldState = TextFieldState()

    private val searchInput = MutableStateFlow("")
    private val useSearchHighlighting = MutableStateFlow(true)

    private var path: String? = null

    val highlightedWords: StateFlow<List<String>> =
        searchInput.asHighlightedWords(useSearchHighlighting).stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(_movieBatch, searchInput) { movieBatch, searchInput ->
                if (searchInput.isNotBlank() && movieBatch?.movies?.isNotEmpty() == true) {
                    FilterUtils.filterMovies(searchInput, movieBatch.movies)
                } else {
                    null
                }
            }.collectLatest {
                _filteredMovies.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getMoviesSearchHistory().collectLatest {
                _searchHistory.value = it
            }
        }
        viewModelScope.launch {
            settingsRepository.getUseSearchHighlighting().collectLatest {
                useSearchHighlighting.value = it
            }
        }
    }

    fun initialize(path: String, movieBatch: MovieBatch?) {
        this.path = path
        _movieBatch.value = movieBatch
    }

    suspend fun updateLoadingState(isForcedUpdate: Boolean) {
        loadingRepository.updateLoadingState(isForcedUpdate)
    }

    fun fetchData() {
        fetchJob?.cancel()
        _movieBatch.value = null
        _preloadBatches.value = emptyMap()
        fetchJob = viewModelScope.launch {
            val batch = apiRepository.fetchMovieBatch(path)
            _movieBatch.value = batch

            val directory = batch.directory

            batch.bookmarks.asSequence()
                .filter { bookmark -> _preloadBatches.value[bookmark] == null }
                .forEach { bookmark ->
                    ensureActive()

                    val result = apiRepository.fetchMovieBatch("$directory$bookmark")

                    _preloadBatches.value += (bookmark to result)
                }
        }
    }

    fun rename(serviceReference: String, newName: String) {
        viewModelScope.launch {
            apiRepository.renameMovie(serviceReference, newName)
            fetchJob?.cancel()
            fetchJob = viewModelScope.launch {
                _movieBatch.value = apiRepository.fetchMovieBatch(path)
            }
        }
    }

    fun move(serviceReference: String, dirName: String) {
        viewModelScope.launch {
            apiRepository.moveMovie(serviceReference, dirName)
            fetchJob?.cancel()
            _preloadBatches.value = emptyMap()
            fetchJob = viewModelScope.launch {
                val batch = apiRepository.fetchMovieBatch(path)
                _movieBatch.value = batch

                val directory = batch.directory

                batch.bookmarks.asSequence()
                    .filter { bookmark -> _preloadBatches.value[bookmark] == null }
                    .forEach { bookmark ->
                        ensureActive()

                        val result = apiRepository.fetchMovieBatch("$directory$bookmark")

                        _preloadBatches.value += (bookmark to result)
                    }
            }
        }
    }

    fun delete(serviceReference: String) {
        viewModelScope.launch {
            apiRepository.deleteMovie(serviceReference)
            fetchJob?.cancel()
            fetchJob = viewModelScope.launch {
                _movieBatch.value = apiRepository.fetchMovieBatch(path)
            }
        }
    }

    fun download(movie: Movie) {
        viewModelScope.launch {
            downloadRepository.downloadMovie(movie)
        }
    }

    fun updateSearchInput() {
        val input = searchFieldState.text.toString()
        if (input.isNotBlank()) {
            viewModelScope.launch {
                searchHistoryRepository.addToMoviesSearchHistory(input)
            }
        }
        searchInput.value = input
    }

    suspend fun buildMovieStreamUrl(fileName: String): String {
        return apiRepository.buildMovieStreamUrl(fileName)
    }

    fun playOnDevice(serviceReference: String) {
        viewModelScope.launch {
            apiRepository.playOnDevice(serviceReference)
        }
    }
}