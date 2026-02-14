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

package io.github.deprec8.enigmadroid.ui.timers

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SearchHistoryRepository
import io.github.deprec8.enigmadroid.data.SettingsRepository
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.timers.Timer
import io.github.deprec8.enigmadroid.model.api.timers.TimerBatch
import io.github.deprec8.enigmadroid.model.api.timers.services.ServiceBatch
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
class TimersViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _filteredTimers = MutableStateFlow<List<Timer>?>(null)
    val filteredTimers: StateFlow<List<Timer>?> = _filteredTimers.asStateFlow()

    private val _timerBatch = MutableStateFlow(TimerBatch())
    val timerBatch: StateFlow<TimerBatch> = _timerBatch.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _services = MutableStateFlow<List<ServiceBatch>>(emptyList())
    val services: StateFlow<List<ServiceBatch>> = _services.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private val _useSearchHighlighting = MutableStateFlow(true)
    val useSearchHighlighting: StateFlow<Boolean> = _useSearchHighlighting.asStateFlow()

    val searchFieldState = TextFieldState()

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(_timerBatch, _searchInput) { timerBatch, searchInput ->
                if (searchInput.isNotBlank() && timerBatch.timers.isNotEmpty()) {
                    searchHistoryRepository.addToTimersSearchHistory(searchInput)
                    FilterUtils.filterTimers(searchInput, timerBatch.timers)
                } else {
                    null
                }
            }.collectLatest {
                _filteredTimers.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getTimersSearchHistory().collectLatest {
                _searchHistory.value = it
            }
        }
        viewModelScope.launch {
            settingsRepository.getUseSearchHighlighting().collectLatest {
                _useSearchHighlighting.value = it
            }
        }
    }

    suspend fun updateLoadingState(isForcedUpdate: Boolean) {
        loadingRepository.updateLoadingState(isForcedUpdate)
    }

    fun fetchData() {
        fetchJob?.cancel()
        _timerBatch.value = TimerBatch()
        _services.value = emptyList()
        fetchJob = viewModelScope.launch {
            _timerBatch.value = apiRepository.fetchTimerBatch()
            _services.value = apiRepository.fetchTimerServiceBatches()
        }
    }


    fun deleteTimer(timer: Timer) {
        viewModelScope.launch {
            apiRepository.deleteTimer(timer)
            fetchData()
        }
    }

    fun toggleTimerStatus(timer: Timer) {
        viewModelScope.launch {
            apiRepository.toggleTimerStatus(timer)
            fetchData()
        }
    }

    fun updateSearchInput() {
        _searchInput.value = searchFieldState.text.toString()
    }

    fun addTimer(newTimer: Timer) {
        viewModelScope.launch {
            apiRepository.addTimer(newTimer)
            fetchData()
        }
    }

    fun editTimer(oldTimer: Timer, newTimer: Timer) {
        viewModelScope.launch {
            apiRepository.editTimer(oldTimer, newTimer)
            fetchData()
        }
    }
}