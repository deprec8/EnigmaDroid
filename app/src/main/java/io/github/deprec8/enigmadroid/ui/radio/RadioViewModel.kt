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

package io.github.deprec8.enigmadroid.ui.radio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.objects.ApiType
import io.github.deprec8.enigmadroid.model.Event
import io.github.deprec8.enigmadroid.model.EventList
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
class RadioViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository
) : ViewModel() {

    var input by mutableStateOf("")
        private set

    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean> = _active.asStateFlow()

    private val _filteredEvents = MutableStateFlow<List<Event>>(emptyList())
    val filteredEvents: StateFlow<List<Event>> = _filteredEvents.asStateFlow()

    private val _allEvents = MutableStateFlow<List<EventList>>(emptyList())
    val allEvents: StateFlow<List<EventList>> = _allEvents.asStateFlow()

    private val _loadingState = MutableStateFlow<Int?>(null)
    val loadingState: StateFlow<Int?> = _loadingState.asStateFlow()

    private val searchInput = MutableStateFlow("")
    private val currentBouquetIndex = MutableStateFlow(0)

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(
                _allEvents,
                searchInput,
                currentBouquetIndex
            ) { allEvents, input, currentBouquetIndex ->
                if (input != "") {
                    FilterUtils.filterEvents(input, allEvents[currentBouquetIndex].events)
                } else {
                    emptyList()
                }
            }.collectLatest {
                _filteredEvents.value = it
            }
        }
    }

    suspend fun updateLoadingState(forceUpdate: Boolean) {
        loadingRepository.updateLoadingState(forceUpdate)
    }

    suspend fun buildStreamUrl(sRef: String): String {
        return apiRepository.buildLiveStreamURL(sRef)
    }

    fun fetchData() {
        fetchJob?.cancel()
        _allEvents.value = emptyList()
        fetchJob = viewModelScope.launch {
            apiRepository.fetchEvents(ApiType.RADIO)
                .collect { events ->
                    _allEvents.value += events
                }
        }
    }

    fun play(sRef: String) {
        viewModelScope.launch {
            apiRepository.play(sRef)
        }
    }

    suspend fun addTimer(event: Event) {
        apiRepository.addTimerForEvent(
            event.serviceReference, event.id
        )
    }

    fun updateInput(newInput: String) {
        input = newInput
    }

    fun updateSearchInput(index: Int) {
        currentBouquetIndex.value = index
        searchInput.value = input
    }

    fun updateActive(isActive: Boolean) {
        _active.value = isActive
    }

}