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

import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.data.repositories.ApiRepository
import io.github.deprec8.enigmadroid.data.repositories.DownloadRepository
import io.github.deprec8.enigmadroid.data.repositories.SearchHistoryRepository
import io.github.deprec8.enigmadroid.model.api.Movie
import io.github.deprec8.enigmadroid.model.api.MovieBatch
import io.github.deprec8.enigmadroid.model.api.search
import io.github.deprec8.enigmadroid.ui.components.viewmodels.SearchableContentViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

class MoviesViewModel(
    @InjectedParam connectedDeviceId: Int? = null,
    @InjectedParam private var path: String? = null,
    @InjectedParam movieBatch: MovieBatch? = null,
    @InjectedParam freeSpace: String? = null,
    private val apiRepository: ApiRepository,
    private val downloadRepository: DownloadRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchableContentViewModel(connectedDeviceId) {

    private val _movieBatch = MutableStateFlow(movieBatch)
    val movieBatch: StateFlow<MovieBatch?> = _movieBatch.asStateFlow()

    private val _preloadBatches = MutableStateFlow<Map<String, MovieBatch>>(emptyMap())
    val preloadBatches: StateFlow<Map<String, MovieBatch>> = _preloadBatches.asStateFlow()

    private val _freeSpace = MutableStateFlow(freeSpace)
    val freeSpace: StateFlow<String?> = _freeSpace.asStateFlow()

    private var preloadJob: Job? = null

    val filteredMovies = combine(_movieBatch, searchInput) { movieBatch, searchInput ->
        movieBatch?.movies?.search(searchInput)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    override val searchHistory = searchHistoryRepository.getMoviesSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun rename(serviceReference: String, newName: String) {
        viewModelScope.launch {
            apiRepository.renameMovie(serviceReference, newName)
            fetchData(true)
        }
    }

    fun move(serviceReference: String, dirName: String) {
        viewModelScope.launch {
            apiRepository.moveMovie(serviceReference, dirName)
            fetchData(true)
        }
    }

    fun delete(serviceReference: String) {
        viewModelScope.launch {
            apiRepository.deleteMovie(serviceReference)
            fetchData(true)
        }
    }

    fun download(movie: Movie) {
        viewModelScope.launch {
            downloadRepository.downloadMovie(movie)
        }
    }

    suspend fun buildMovieStreamUrl(fileName: String): String {
        return apiRepository.buildMovieStreamUrl(fileName)
    }

    fun playOnDevice(serviceReference: String) {
        viewModelScope.launch {
            apiRepository.playOnDevice(serviceReference)
        }
    }

    override fun onAddToSearchHistory(input: String) {
        viewModelScope.launch {
            searchHistoryRepository.addToMoviesSearchHistory(input)
        }
    }

    override fun onClearData() {
        _movieBatch.value = null
        _preloadBatches.value = emptyMap()
        _freeSpace.value = null
    }

    override suspend fun onGetData() {
        val batch = apiRepository.fetchMovieBatch(path)
        _movieBatch.value = batch

        _freeSpace.value = apiRepository.fetchFreeSpace(batch.directory)

        preloadJob?.cancel()
        preloadJob = viewModelScope.launch {
            batch.bookmarks.asSequence()
                .filter { bookmark -> _preloadBatches.value[bookmark] == null }
                .forEach { bookmark ->
                    ensureActive()

                    val result = apiRepository.fetchMovieBatch("${batch.directory}$bookmark")

                    _preloadBatches.value += (bookmark to result)
                }
        }
    }

    override fun shouldGetData(): Boolean {
        return _movieBatch.value == null || _preloadBatches.value.isEmpty() || _freeSpace.value == null
    }
}