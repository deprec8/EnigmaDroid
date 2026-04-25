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

package io.github.deprec8.enigmadroid.ui.epg.serviceEpg

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.common.enums.LoadingState
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SearchHistoryRepository
import io.github.deprec8.enigmadroid.data.SettingsRepository
import io.github.deprec8.enigmadroid.model.api.Event
import io.github.deprec8.enigmadroid.model.api.EventBatch
import io.github.deprec8.enigmadroid.ui.components.search.asHighlightedWords
import io.github.deprec8.enigmadroid.utils.FilterUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceEpgViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val settingsRepository: SettingsRepository,
    private val devicesRepository: DevicesRepository
) : ViewModel() {

    private val _eventBatch = MutableStateFlow<EventBatch?>(null)
    val eventBatch: StateFlow<EventBatch?> = _eventBatch.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _filteredEvents = MutableStateFlow<List<Event>?>(null)
    val filteredEvents: StateFlow<List<Event>?> = _filteredEvents.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    val searchFieldState = TextFieldState()

    private val searchInput = MutableStateFlow("")
    private val useSearchHighlighting = MutableStateFlow(true)

    val highlightedWords: StateFlow<List<String>> =
        searchInput.asHighlightedWords(useSearchHighlighting).stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var fetchJob: Job? = null

    private var serviceReference = ""

    private var loadedDeviceId: Int? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(_eventBatch, searchInput) { eventBatch, searchInput ->
                if (searchInput.isNotBlank() && eventBatch?.events?.isNotEmpty() == true) {
                    FilterUtils.filterEvents(searchInput, eventBatch.events)
                } else {
                    null
                }
            }.collectLatest {
                _filteredEvents.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getServiceEpgSearchHistory().collectLatest {
                _searchHistory.value = it
            }
        }
        viewModelScope.launch {
            settingsRepository.getUseSearchHighlighting().collectLatest {
                useSearchHighlighting.value = it
            }
        }
    }

    fun initialize(serviceReference: String) {
        this.serviceReference = serviceReference
    }

    suspend fun updateLoadingState(isForcedUpdate: Boolean) {
        loadingRepository.updateLoadingState(isForcedUpdate)
    }

    fun fetchData(isForced: Boolean = false) {
        viewModelScope.launch {
            val currentDeviceId = devicesRepository.getCurrentDeviceId().first()
            if (currentDeviceId != loadedDeviceId || isForced) {
                _eventBatch.value = null
                loadedDeviceId = currentDeviceId
            }

            if (_eventBatch.value == null) {
                fetchJob?.cancel()
                fetchJob = launch {
                    _eventBatch.value = apiRepository.fetchServiceEpgBatch(serviceReference)
                }
            }
        }
    }

    fun addTimerForEvent(event: Event) {
        viewModelScope.launch {
            apiRepository.addTimerForEvent(
                event.serviceReference, event.id
            )
        }
    }

    fun updateSearchInput() {
        val input = searchFieldState.text.toString()
        if (input.isNotBlank()) {
            viewModelScope.launch {
                searchHistoryRepository.addToServiceEpgSearchHistory(input)
            }
        }
        searchInput.value = input
    }
}