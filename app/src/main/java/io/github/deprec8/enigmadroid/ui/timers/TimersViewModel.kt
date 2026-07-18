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

import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.data.repositories.ApiRepository
import io.github.deprec8.enigmadroid.data.repositories.SearchHistoryRepository
import io.github.deprec8.enigmadroid.model.api.ServiceBatchSet
import io.github.deprec8.enigmadroid.model.api.Timer
import io.github.deprec8.enigmadroid.model.api.TimerBatch
import io.github.deprec8.enigmadroid.model.api.search
import io.github.deprec8.enigmadroid.ui.components.viewmodels.SearchableContentViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimersViewModel(
    private val apiRepository: ApiRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchableContentViewModel() {

    private val _timerBatch = MutableStateFlow<TimerBatch?>(null)
    val timerBatch: StateFlow<TimerBatch?> = _timerBatch.asStateFlow()

    private val _serviceBatchSet = MutableStateFlow<ServiceBatchSet?>(null)
    val serviceBatchSet: StateFlow<ServiceBatchSet?> = _serviceBatchSet.asStateFlow()

    val filteredTimers = combine(_timerBatch, searchInput) { timerBatch, searchInput ->
        timerBatch?.timers?.search(searchInput)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    override val searchHistory = searchHistoryRepository.getTimersSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun deleteTimer(timer: Timer) {
        viewModelScope.launch {
            apiRepository.deleteTimer(timer)
            fetchJob?.cancel()
            fetchJob = viewModelScope.launch {
                _timerBatch.value = apiRepository.fetchTimerBatch()
            }
        }
    }

    fun toggleTimerStatus(timer: Timer) {
        viewModelScope.launch {
            apiRepository.toggleTimerStatus(timer)
            fetchJob?.cancel()
            fetchJob = viewModelScope.launch {
                _timerBatch.value = apiRepository.fetchTimerBatch()
            }
        }
    }


    fun addTimer(newTimer: Timer) {
        viewModelScope.launch {
            apiRepository.addTimer(newTimer)
            fetchJob?.cancel()
            fetchJob = viewModelScope.launch {
                _timerBatch.value = apiRepository.fetchTimerBatch()
            }
        }
    }

    fun editTimer(oldTimer: Timer, newTimer: Timer) {
        viewModelScope.launch {
            apiRepository.editTimer(oldTimer, newTimer)
            fetchJob?.cancel()
            fetchJob = viewModelScope.launch {
                _timerBatch.value = apiRepository.fetchTimerBatch()
            }
        }
    }

    override fun onAddToSearchHistory(input: String) {
        viewModelScope.launch {
            searchHistoryRepository.addToTimersSearchHistory(input)
        }
    }

    override fun onClearData() {
        _timerBatch.value = null
        _serviceBatchSet.value = null
    }

    override suspend fun onGetData() {
        _timerBatch.value = apiRepository.fetchTimerBatch()
        _serviceBatchSet.value = apiRepository.fetchServiceBatchSet()
    }

    override fun shouldGetData(): Boolean {
        return _timerBatch.value == null || _serviceBatchSet.value == null
    }
}