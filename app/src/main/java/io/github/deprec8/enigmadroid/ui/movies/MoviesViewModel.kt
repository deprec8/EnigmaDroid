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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.DownloadRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SearchHistoryRepository
import io.github.deprec8.enigmadroid.model.Movie
import io.github.deprec8.enigmadroid.model.MovieList
import io.github.deprec8.enigmadroid.utils.FilterUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository,
    private val downloadRepository: DownloadRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    var input by mutableStateOf("")
        private set

    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean> = _active.asStateFlow()

    private val _filteredMovies = MutableStateFlow<List<Movie>?>(null)
    val filteredMovies: StateFlow<List<Movie>?> = _filteredMovies.asStateFlow()

    private val _movies = MutableStateFlow<List<MovieList>>(emptyList())
    val movies: StateFlow<List<MovieList>> = _movies.asStateFlow()

    private val _loadingState = MutableStateFlow<Int?>(null)
    val loadingState: StateFlow<Int?> = _loadingState.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(_movies, _searchInput) { movies, input ->
                if (input != "") {
                    searchHistoryRepository.addToMoviesSearchHistory(input)
                    FilterUtils.filterMovies(input, movies.flatMap { it.movies })
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
    }

    suspend fun updateLoadingState(forceUpdate: Boolean) {
        loadingRepository.updateLoadingState(forceUpdate)
    }

    fun fetchData() {
        fetchJob?.cancel()
        _movies.value = emptyList()
        fetchJob = viewModelScope.launch {
            apiRepository.fetchMovies().collect { movies ->
                _movies.value += movies
            }
        }
    }

    fun rename(sRef: String, newName: String) {
        viewModelScope.launch {
            apiRepository.renameMovie(sRef, newName)
            fetchData()
        }
    }

    fun move(sRef: String, dirname: String) {
        viewModelScope.launch {
            apiRepository.moveMovie(sRef, dirname)
            fetchData()
        }
    }

    fun delete(sRef: String) {
        viewModelScope.launch {
            apiRepository.deleteMovie(sRef)
            fetchData()
        }
    }

    fun downloadMovie(movie: Movie) {
        viewModelScope.launch {
            downloadRepository.downloadMovie(movie)
        }
    }

    fun updateSearchInput() {
        _searchInput.value = input
    }

    suspend fun buildStreamUrl(sRef: String): String {
        return apiRepository.buildMovieStreamURL(sRef)
    }

    fun updateInput(newInput: String) {
        input = newInput
    }

    fun updateActive(isActive: Boolean) {
        _active.value = isActive
    }

    fun play(sRef: String) {
        viewModelScope.launch {
            apiRepository.play(sRef)
        }
    }

}