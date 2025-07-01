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

package io.github.deprec8.enigmadroid.ui.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.data.DownloadRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SettingsRepository
import io.github.deprec8.enigmadroid.data.objects.RemoteButtons
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val devicesRepository: DevicesRepository,
    private val loadingRepository: LoadingRepository,
    private val downloadRepository: DownloadRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _loadingState = MutableStateFlow<Int?>(null)
    val loadingState: StateFlow<Int?> = _loadingState.asStateFlow()

    private val _currentDevice = MutableStateFlow<Device?>(null)
    val currentDevice: StateFlow<Device?> = _currentDevice.asStateFlow()

    private val _remoteVibration = MutableStateFlow(false)
    val remoteVibration: StateFlow<Boolean> = _remoteVibration.asStateFlow()

    init {
        viewModelScope.launch {
            devicesRepository.getCurrentDevice().collectLatest { currentDevice ->
                _currentDevice.value = currentDevice
            }
        }
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state ?: 3
            }
        }
        viewModelScope.launch {
            settingsRepository.getRemoteVibration().collectLatest { vibration ->
                _remoteVibration.value = vibration
            }
        }
    }

    private fun remoteCall(command: Int) {
        viewModelScope.launch {
            apiRepository.remoteCall(command)
        }
    }

    suspend fun updateLoadingState(forceUpdate: Boolean) {
        loadingRepository.updateLoadingState(forceUpdate)
    }

    fun fetchScreenshot() {
        viewModelScope.launch {
            downloadRepository.fetchScreenshot()
        }
    }

    // Volume
    fun volUP() {
        remoteCall(RemoteButtons.VOLUME_UP)
    }

    fun volDown() {
        remoteCall(RemoteButtons.VOLUME_DOWN)
    }

    fun volMute() {
        remoteCall(RemoteButtons.VOLUME_MUTE)
    }

    // Channel
    fun chUP() {
        remoteCall(RemoteButtons.NEXT_CHANNEL)
    }

    fun chDown() {
        remoteCall(RemoteButtons.PREVIOUS_CHANNEL)
    }

    // Play Control
    fun play() {
        remoteCall(RemoteButtons.PLAY)
    }

    fun pause() {
        remoteCall(RemoteButtons.PAUSE)
    }

    fun forward() {
        remoteCall(RemoteButtons.FORWARD)
    }

    fun rewind() {
        remoteCall(RemoteButtons.REWIND)
    }

    // Main Buttons
    fun ok() {
        remoteCall(RemoteButtons.OK)
    }

    fun menu() {
        remoteCall(RemoteButtons.MENU)
    }

    fun audio() {
        remoteCall(RemoteButtons.AUDIO)
    }

    fun epg() {
        remoteCall(RemoteButtons.EPG)
    }

    fun pvr() {
        remoteCall(RemoteButtons.PVR)
    }

    fun power(command: Int) {
        viewModelScope.launch {
            apiRepository.setPowerState(command)
            updateLoadingState(true)
        }
    }

    fun help() {
        remoteCall(RemoteButtons.HELP)
    }

    fun exit() {
        remoteCall(RemoteButtons.EXIT)
    }

    fun tv() {
        remoteCall(RemoteButtons.TV)
    }

    fun radio() {
        remoteCall(RemoteButtons.RADIO)
    }

    fun record() {
        remoteCall(RemoteButtons.RECORD)
    }

    fun stop() {
        remoteCall(RemoteButtons.STOP)
    }

    // Arrows
    fun aUP() {
        remoteCall(RemoteButtons.ARROW_UP)
    }

    fun aDown() {
        remoteCall(RemoteButtons.ARROW_DOWN)
    }

    fun aLeft() {
        remoteCall(RemoteButtons.ARROW_LEFT)
    }

    fun aRight() {
        remoteCall(RemoteButtons.ARROW_RIGHT)
    }

    // Number pad
    fun number(number: Int) {
        if (number == 0) {
            remoteCall(11)
        } else {
            remoteCall(number + 1)
        }
    }

    // Colors
    fun red() {
        remoteCall(RemoteButtons.COLOR_RED)
    }

    fun green() {
        remoteCall(RemoteButtons.COLOR_GREEN)
    }

    fun yellow() {
        remoteCall(RemoteButtons.COLOR_YELLOW)
    }

    fun blue() {
        remoteCall(RemoteButtons.COLOR_BLUE)
    }

    fun info() {
        remoteCall(RemoteButtons.INFO)
    }

    fun text() {
        remoteCall(RemoteButtons.TEXT)
    }

    fun bouUP() {
        remoteCall(RemoteButtons.NEXT_BOUQUET)
    }

    fun bouDOWN() {
        remoteCall(RemoteButtons.PREVIOUS_BOUQUET)
    }


}