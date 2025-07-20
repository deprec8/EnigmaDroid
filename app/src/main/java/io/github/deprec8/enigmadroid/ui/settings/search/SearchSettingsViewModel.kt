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

package io.github.deprec8.enigmadroid.ui.settings.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.SearchHistoryRepository
import io.github.deprec8.enigmadroid.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchSettingsViewModel @Inject constructor(
    private var searchHistoryRepository: SearchHistoryRepository,
    private var settingsRepository: SettingsRepository
) : ViewModel() {

    private val _tvSearchHistory = MutableStateFlow<List<String>>(emptyList())
    val tvSearchHistory = _tvSearchHistory.asStateFlow()

    private val _radioSearchHistory = MutableStateFlow<List<String>>(emptyList())
    val radioSearchHistory = _radioSearchHistory.asStateFlow()

    private val _moviesSearchHistory = MutableStateFlow<List<String>>(emptyList())
    val moviesSearchHistory = _moviesSearchHistory.asStateFlow()

    private val _timersSearchHistory = MutableStateFlow<List<String>>(emptyList())
    val timersSearchHistory = _timersSearchHistory.asStateFlow()

    private val _tvEPGSearchHistory = MutableStateFlow<List<String>>(emptyList())
    val tvEPGSearchHistory = _tvEPGSearchHistory.asStateFlow()

    private val _radioEPGSearchHistory = MutableStateFlow<List<String>>(emptyList())
    val radioEPGSearchHistory = _radioEPGSearchHistory.asStateFlow()

    private val _useSearchHistory = MutableStateFlow<Boolean?>(null)
    val useSearchHistories = _useSearchHistory.asStateFlow()

    private val _useSearchHighlighting = MutableStateFlow<Boolean?>(null)
    val useSearchHighlighting = _useSearchHighlighting.asStateFlow()

    init {
        viewModelScope.launch {
            searchHistoryRepository.getUseSearchHistory().collect {
                _useSearchHistory.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getTVSearchHistory().collect {
                _tvSearchHistory.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getRadioSearchHistory().collect {
                _radioSearchHistory.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getMoviesSearchHistory().collect {
                _moviesSearchHistory.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getTimersSearchHistory().collect {
                _timersSearchHistory.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getTVEPGSearchHistory().collect {
                _tvEPGSearchHistory.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getRadioEPGSearchHistory().collect {
                _radioEPGSearchHistory.value = it
            }
        }
        viewModelScope.launch {
            settingsRepository.getUseSearchHighlighting().collect {
                _useSearchHighlighting.value = it
            }
        }
    }

    fun setUseSearchHistory(value: Boolean) {
        viewModelScope.launch {
            searchHistoryRepository.setUseSearchHistory(value)
        }
    }

    fun setUseSearchHighlighting(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseSearchHighlighting(value)
        }
    }

    fun clearSearchHistory(
        tv: Boolean,
        radio: Boolean,
        movies: Boolean,
        timers: Boolean,
        tvEPG: Boolean,
        radioEPG: Boolean
    ) {
        viewModelScope.launch {
            if (tv) {
                searchHistoryRepository.clearTVSearchHistory()
            }
            if (radio) {
                searchHistoryRepository.clearRadioSearchHistory()
            }
            if (movies) {
                searchHistoryRepository.clearMoviesSearchHistory()
            }
            if (timers) {
                searchHistoryRepository.clearTimersSearchHistory()
            }
            if (tvEPG) {
                searchHistoryRepository.clearTVEPGSearchHistory()
            }
            if (radioEPG) {
                searchHistoryRepository.clearRadioEPGSearchHistory()
            }
        }
    }
}