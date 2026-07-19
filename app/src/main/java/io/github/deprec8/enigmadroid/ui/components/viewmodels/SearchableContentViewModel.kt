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

package io.github.deprec8.enigmadroid.ui.components.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.inject

abstract class SearchableContentViewModel : ContentViewModel() {

    private val settingsRepository: SettingsRepository by inject()

    protected val useSearchHighlighting = settingsRepository.getUseSearchHighlighting().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val searchFieldState = TextFieldState()

    protected val searchInput = MutableStateFlow("")

    val highlightedWords = combine(searchInput, useSearchHighlighting) { input, enabled ->
        if (enabled) input.split(" ").filter { it.isNotBlank() } else emptyList()
    }.stateIn(
        viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
    )

    abstract val searchHistory: StateFlow<List<String>>

    fun updateSearchInput() {
        val input = searchFieldState.text.toString()
        if (input.isNotBlank()) {
            onAddToSearchHistory(input)
        }
        searchInput.value = input
    }

    abstract fun onAddToSearchHistory(input: String)
}