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

import android.net.Uri
import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.common.enums.ContentType
import io.github.deprec8.enigmadroid.data.repositories.ApiRepository
import io.github.deprec8.enigmadroid.data.repositories.DownloadRepository
import io.github.deprec8.enigmadroid.model.api.Movie
import io.github.deprec8.enigmadroid.model.api.MovieBatch
import io.github.deprec8.enigmadroid.model.api.search
import io.github.deprec8.enigmadroid.ui.components.viewmodels.SearchableContentViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

class MoviesViewModel(
    @InjectedParam private var path: String? = null,
    private val apiRepository: ApiRepository,
    private val downloadRepository: DownloadRepository
) : SearchableContentViewModel(ContentType.Movies) {

    private val _movieBatch = MutableStateFlow<MovieBatch?>(null)
    val movieBatch: StateFlow<MovieBatch?> = _movieBatch.asStateFlow()

    private val _freeSpace = MutableStateFlow<String?>(null)
    val freeSpace: StateFlow<String?> = _freeSpace.asStateFlow()

    val filteredMovies = combine(_movieBatch, searchInput) { movieBatch, searchInput ->
        movieBatch?.movies?.search(searchInput)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    fun rename(serviceReference: String, newName: String) {
        viewModelScope.launch {
            apiRepository.renameMovie(serviceReference, newName)
            fetchData(false)
        }
    }

    fun move(serviceReference: String, dirName: String) {
        viewModelScope.launch {
            apiRepository.moveMovie(serviceReference, dirName)
            fetchData(false)
        }
    }

    fun delete(serviceReference: String) {
        viewModelScope.launch {
            apiRepository.deleteMovie(serviceReference)
            fetchData(false)
        }
    }

    fun download(movie: Movie) {
        viewModelScope.launch {
            downloadRepository.downloadMovie(movie)
        }
    }

    suspend fun buildMovieStreamUri(fileName: String): Uri? {
        return apiRepository.buildMovieStreamUri(fileName)
    }

    fun playOnDevice(serviceReference: String) {
        viewModelScope.launch {
            apiRepository.playOnDevice(serviceReference)
        }
    }

    override fun onClearData() {
        _movieBatch.value = null
        _freeSpace.value = null
    }

    override suspend fun onGetData() {
        val batch = apiRepository.fetchMovieBatch(path)
        _movieBatch.value = batch

        _freeSpace.value = apiRepository.fetchFreeSpace(batch.directory)
    }
}