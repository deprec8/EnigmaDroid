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

package io.github.deprec8.enigmadroid.ui.timers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SearchHistoryRepository
import io.github.deprec8.enigmadroid.data.SettingsRepository
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.ServiceList
import io.github.deprec8.enigmadroid.model.Timer
import io.github.deprec8.enigmadroid.model.TimerList
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

    var input by mutableStateOf("")
        private set

    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean> = _active.asStateFlow()

    private val _filteredTimers = MutableStateFlow<List<Timer>?>(null)
    val filteredTimers: StateFlow<List<Timer>?> = _filteredTimers.asStateFlow()

    private val _timerList = MutableStateFlow(TimerList())
    val timerList: StateFlow<TimerList> = _timerList.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _services = MutableStateFlow<List<ServiceList>>(emptyList())
    val services: StateFlow<List<ServiceList>> = _services.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private val _useSearchHighlighting = MutableStateFlow(true)
    val useSearchHighlighting: StateFlow<Boolean> = _useSearchHighlighting.asStateFlow()

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(_timerList, _searchInput) { timerList, input ->
                if (input != "") {
                    searchHistoryRepository.addToTimersSearchHistory(input)
                    FilterUtils.filterTimers(input, timerList.timers)
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

    suspend fun updateLoadingState(forceUpdate: Boolean) {
        loadingRepository.updateLoadingState(forceUpdate)
    }

    fun fetchData() {
        fetchJob?.cancel()
        _timerList.value = TimerList()
        _services.value = emptyList()
        fetchJob = viewModelScope.launch {
            _timerList.value = apiRepository.fetchTimerList()
            _services.value = apiRepository.fetchTimerServices()
        }
    }

    fun updateInput(newInput: String) {
        input = newInput
    }

    fun deleteTimer(timer: Timer) {
        viewModelScope.launch {
            apiRepository.deleteTimer(timer)
            fetchData()
        }
    }

    fun updateActive(isActive: Boolean) {
        _active.value = isActive
    }

    fun updateSearchInput() {
        _searchInput.value = input
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