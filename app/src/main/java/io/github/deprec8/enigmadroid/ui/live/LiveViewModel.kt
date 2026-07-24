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

package io.github.deprec8.enigmadroid.ui.live

import android.net.Uri
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

class LiveViewModel(
    @InjectedParam private val contentType: ContentType, private val apiRepository: ApiRepository
) : SearchableContentViewModel(contentType) {

    private val _eventBatchesResult = MutableStateFlow<Result<List<EventBatch>>?>(null)
    val eventBatchesResult: StateFlow<Result<List<EventBatch>>?> = _eventBatchesResult.asStateFlow()

    private val currentBouquetIndex = MutableStateFlow(0)

    val filteredEvents = combine(
        _eventBatchesResult, searchInput, currentBouquetIndex
    ) { eventBatchesResult, searchInput, currentBouquetIndex ->
        eventBatchesResult?.getOrNull()?.getOrNull(currentBouquetIndex)?.events?.search(searchInput)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    suspend fun buildLiveStreamUri(serviceReference: String): Uri? {
        return apiRepository.buildLiveStreamUri(serviceReference)
    }

    fun playOnDevice(serviceReference: String) {
        viewModelScope.launch {
            apiRepository.playOnDevice(serviceReference)
        }
    }

    fun addTimerForEvent(event: Event) {
        viewModelScope.launch {
            apiRepository.addTimerForEvent(
                event.serviceReference, event.id
            )
        }
    }

    fun updateCurrentBouquetIndex(index: Int) {
        currentBouquetIndex.value = index
    }

    override fun onClearData() {
        _eventBatchesResult.value = null
    }

    override suspend fun onGetData() {
        val accumulatedBatches = mutableListOf<EventBatch>()
        apiRepository.fetchEventBatches(contentType).collect { result ->
            result.onSuccess { batch ->
                accumulatedBatches.add(batch)
                _eventBatchesResult.value = Result.success(accumulatedBatches.toList())
            }.onFailure {
                _eventBatchesResult.value = Result.failure(it)
            }
        }
    }
}