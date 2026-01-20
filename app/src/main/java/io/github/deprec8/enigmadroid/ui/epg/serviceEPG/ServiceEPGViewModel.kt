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

package io.github.deprec8.enigmadroid.ui.epg.serviceEPG

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.Event
import io.github.deprec8.enigmadroid.model.api.EventList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceEPGViewModel @Inject constructor(
    private val apiRepository: ApiRepository, private val loadingRepository: LoadingRepository
) : ViewModel() {

    private val _epg = MutableStateFlow(EventList())
    val epg: StateFlow<EventList> = _epg.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
    }

    suspend fun updateLoadingState(forceUpdate: Boolean) {
        loadingRepository.updateLoadingState(forceUpdate)
    }

    fun fetchData(sRef: String) {
        fetchJob?.cancel()
        _epg.value = EventList()
        fetchJob = viewModelScope.launch {
            _epg.value = apiRepository.fetchServiceEPG(sRef)
        }
    }

    fun addTimer(event: Event) {
        viewModelScope.launch {
            apiRepository.addTimerForEvent(
                event.serviceReference, event.id
            )
        }
    }
}