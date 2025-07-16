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

package io.github.deprec8.enigmadroid.ui.tvEPG

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SearchHistoryRepository
import io.github.deprec8.enigmadroid.data.objects.ApiType
import io.github.deprec8.enigmadroid.model.EPGEvent
import io.github.deprec8.enigmadroid.model.EPGEventList
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
class TVEPGViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _epgs = MutableStateFlow(listOf<EPGEventList>())
    val epgs: StateFlow<List<EPGEventList>> = _epgs.asStateFlow()

    var input by mutableStateOf("")
        private set

    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean> = _active.asStateFlow()

    private val _filteredEPGEvents = MutableStateFlow<List<EPGEvent>?>(null)
    val filteredEPGEvents: StateFlow<List<EPGEvent>?> = _filteredEPGEvents.asStateFlow()

    private val _loadingState = MutableStateFlow<Int?>(null)
    val loadingState: StateFlow<Int?> = _loadingState.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _searchInput = MutableStateFlow("")

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(_epgs, _searchInput) { epgs, input ->
                if (input != "") {
                    searchHistoryRepository.addToTVEPGSearchHistory(input)
                    FilterUtils.filterEPGEvents(input, epgs.flatMap { it.events })
                } else {
                    null
                }
            }.collectLatest {
                _filteredEPGEvents.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getTVEPGSearchHistory().collectLatest {
                _searchHistory.value = it
            }
        }
    }

    suspend fun updateLoadingState(forceUpdate: Boolean) {
        loadingRepository.updateLoadingState(forceUpdate)
    }

    fun fetchData() {
        fetchJob?.cancel()
        _epgs.value = emptyList()
        fetchJob = viewModelScope.launch {
            apiRepository.fetchEPG(ApiType.TV).collect { epgs ->
                _epgs.value += epgs
            }
        }
    }

    fun addTimer(event: EPGEvent) {
        viewModelScope.launch {
            apiRepository.addTimerForEvent(
                event.serviceReference, event.id
            )
        }
    }

    fun updateInput(newInput: String) {
        input = newInput
    }

    fun updateSearchInput() {
        _searchInput.value = input
    }

    fun updateActive(isActive: Boolean) {
        _active.value = isActive
    }

}