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

package io.github.deprec8.enigmadroid.ui.settings.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.data.repositories.SearchHistoryRepository
import io.github.deprec8.enigmadroid.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchSettingsViewModel(
    private var searchHistoryRepository: SearchHistoryRepository,
    private var settingsRepository: SettingsRepository
) : ViewModel() {

    val tvSearchHistory = searchHistoryRepository.getTvSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val radioSearchHistory = searchHistoryRepository.getRadioSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val moviesSearchHistory = searchHistoryRepository.getMoviesSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val timersSearchHistory = searchHistoryRepository.getTimersSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val tvEpgSearchHistory = searchHistoryRepository.getTvEpgSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val radioEpgSearchHistory = searchHistoryRepository.getRadioEpgSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val serviceEpgSearchHistory = searchHistoryRepository.getServiceEpgSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val useSearchHistory = searchHistoryRepository.getUseSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val useSearchHighlighting = settingsRepository.getUseSearchHighlighting().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    fun setUseSearchHistory(useSearchHistory: Boolean) {
        viewModelScope.launch {
            searchHistoryRepository.setUseSearchHistory(useSearchHistory)
        }
    }

    fun setUseSearchHighlighting(useSearchHighlighting: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseSearchHighlighting(useSearchHighlighting)
        }
    }

    fun clearSearchHistory(
        tv: Boolean,
        radio: Boolean,
        movies: Boolean,
        timers: Boolean,
        tvEpg: Boolean,
        radioEpg: Boolean,
        serviceEpg: Boolean
    ) {
        viewModelScope.launch {
            if (tv) {
                searchHistoryRepository.clearTvSearchHistory()
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
            if (tvEpg) {
                searchHistoryRepository.clearTvEpgSearchHistory()
            }
            if (radioEpg) {
                searchHistoryRepository.clearRadioEpgSearchHistory()
            }
            if (serviceEpg) {
                searchHistoryRepository.clearServiceEpgSearchHistory()
            }
        }
    }
}