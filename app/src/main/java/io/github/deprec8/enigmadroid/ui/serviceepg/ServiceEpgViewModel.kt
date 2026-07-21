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

package io.github.deprec8.enigmadroid.ui.serviceepg

import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.common.enums.ContentType
import io.github.deprec8.enigmadroid.data.repositories.ApiRepository
import io.github.deprec8.enigmadroid.model.api.Event
import io.github.deprec8.enigmadroid.model.api.EventBatch
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

class ServiceEpgViewModel(
    @InjectedParam private val serviceReference: String, private val apiRepository: ApiRepository
) : SearchableContentViewModel(ContentType.ServiceEpg) {

    private val _eventBatch = MutableStateFlow<EventBatch?>(null)
    val eventBatch: StateFlow<EventBatch?> = _eventBatch.asStateFlow()

    val filteredEvents = combine(_eventBatch, searchInput) { eventBatch, searchInput ->
        eventBatch?.events?.search(searchInput)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    fun addTimerForEvent(event: Event) {
        viewModelScope.launch {
            apiRepository.addTimerForEvent(
                event.serviceReference, event.id
            )
        }
    }

    override fun onClearData() {
        _eventBatch.value = null
    }

    override suspend fun onGetData() {
        _eventBatch.value = apiRepository.fetchServiceEpgBatch(serviceReference)
    }
}