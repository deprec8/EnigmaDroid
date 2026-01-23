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

package io.github.deprec8.enigmadroid.ui.settings.remoteControl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteControlSettingsViewModel @Inject constructor(private var settingsRepository: SettingsRepository) :
    ViewModel() {

    private val _remoteVibration = MutableStateFlow<Boolean?>(null)
    val remoteVibration: StateFlow<Boolean?> = _remoteVibration.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getRemoteControlVibration().collectLatest {
                _remoteVibration.value = it
            }
        }
    }

    fun setRemoteVibration(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setRemoteControlVibration(value)
        }
    }
}