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
import io.github.deprec8.enigmadroid.common.enums.ContentType
import io.github.deprec8.enigmadroid.data.repositories.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchSettingsViewModel(
    private var searchRepository: SearchRepository
) : ViewModel() {

    val typesWithHistory = searchRepository.getTypesWithHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet()
    )

    val useSearchHistories = searchRepository.getUseHistories().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val useSearchHighlighting = searchRepository.getUseHighlighting().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    private val _selectedTypes = MutableStateFlow<Set<ContentType>>(emptySet())
    val selectedTypes = _selectedTypes.asStateFlow()

    fun toggleTypeSelection(type: ContentType) {
        _selectedTypes.value = if (_selectedTypes.value.contains(type)) {
            _selectedTypes.value - type
        } else {
            _selectedTypes.value + type
        }
    }

    fun toggleAllSelection(availableTypes: Set<ContentType>) {
        _selectedTypes.value = if (_selectedTypes.value.size == availableTypes.size) {
            emptySet()
        } else {
            availableTypes
        }
    }

    fun setUseSearchHistory(value: Boolean) {
        viewModelScope.launch {
            searchRepository.setUseHistories(value)
        }
    }

    fun setUseSearchHighlighting(value: Boolean) {
        viewModelScope.launch {
            searchRepository.setUseHighlighting(value)
        }
    }

    fun clearSelectedHistories() {
        viewModelScope.launch {
            searchRepository.clearHistories(_selectedTypes.value)
            _selectedTypes.value = emptySet()
        }
    }
}