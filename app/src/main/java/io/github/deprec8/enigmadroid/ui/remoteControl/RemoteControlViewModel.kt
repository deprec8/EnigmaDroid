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

package io.github.deprec8.enigmadroid.ui.remoteControl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.data.DownloadRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SettingsRepository
import io.github.deprec8.enigmadroid.data.objects.LoadingState
import io.github.deprec8.enigmadroid.data.objects.RemoteControlButtons
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val devicesRepository: DevicesRepository,
    private val loadingRepository: LoadingRepository,
    private val downloadRepository: DownloadRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _currentDevice = MutableStateFlow<Device?>(null)
    val currentDevice: StateFlow<Device?> = _currentDevice.asStateFlow()

    private val _remoteControlVibration = MutableStateFlow(false)
    val remoteVibration: StateFlow<Boolean> = _remoteControlVibration.asStateFlow()

    init {
        viewModelScope.launch {
            devicesRepository.getCurrentDevice().collectLatest { currentDevice ->
                _currentDevice.value = currentDevice
            }
        }
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            settingsRepository.getRemoteControlVibration().collectLatest { value ->
                _remoteControlVibration.value = value
            }
        }
    }

    private fun remoteCall(button: RemoteControlButtons) {
        viewModelScope.launch {
            apiRepository.remoteControlCall(button)
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
        remoteCall(RemoteControlButtons.VOLUME_UP)
    }

    fun volDown() {
        remoteCall(RemoteControlButtons.VOLUME_DOWN)
    }

    fun volMute() {
        remoteCall(RemoteControlButtons.VOLUME_MUTE)
    }

    // Channel
    fun chUP() {
        remoteCall(RemoteControlButtons.NEXT_CHANNEL)
    }

    fun chDown() {
        remoteCall(RemoteControlButtons.PREVIOUS_CHANNEL)
    }

    // Play Control
    fun play() {
        remoteCall(RemoteControlButtons.PLAY)
    }

    fun pause() {
        remoteCall(RemoteControlButtons.PAUSE)
    }

    fun forward() {
        remoteCall(RemoteControlButtons.FORWARD)
    }

    fun rewind() {
        remoteCall(RemoteControlButtons.REWIND)
    }

    // Main Buttons
    fun ok() {
        remoteCall(RemoteControlButtons.OK)
    }

    fun menu() {
        remoteCall(RemoteControlButtons.MENU)
    }

    fun audio() {
        remoteCall(RemoteControlButtons.AUDIO)
    }

    fun epg() {
        remoteCall(RemoteControlButtons.EPG)
    }

    fun pvr() {
        remoteCall(RemoteControlButtons.PVR)
    }

    fun power(command: Int) {
        viewModelScope.launch {
            apiRepository.setPowerState(command)
            updateLoadingState(true)
        }
    }

    fun help() {
        remoteCall(RemoteControlButtons.HELP)
    }

    fun exit() {
        remoteCall(RemoteControlButtons.EXIT)
    }

    fun tv() {
        remoteCall(RemoteControlButtons.TV)
    }

    fun radio() {
        remoteCall(RemoteControlButtons.RADIO)
    }

    fun record() {
        remoteCall(RemoteControlButtons.RECORD)
    }

    fun stop() {
        remoteCall(RemoteControlButtons.STOP)
    }

    // Arrows
    fun aUP() {
        remoteCall(RemoteControlButtons.ARROW_UP)
    }

    fun aDown() {
        remoteCall(RemoteControlButtons.ARROW_DOWN)
    }

    fun aLeft() {
        remoteCall(RemoteControlButtons.ARROW_LEFT)
    }

    fun aRight() {
        remoteCall(RemoteControlButtons.ARROW_RIGHT)
    }

    // Number pad
    fun number(button: RemoteControlButtons) {
        remoteCall(button)
    }

    // Colors
    fun red() {
        remoteCall(RemoteControlButtons.COLOR_RED)
    }

    fun green() {
        remoteCall(RemoteControlButtons.COLOR_GREEN)
    }

    fun yellow() {
        remoteCall(RemoteControlButtons.COLOR_YELLOW)
    }

    fun blue() {
        remoteCall(RemoteControlButtons.COLOR_BLUE)
    }

    fun info() {
        remoteCall(RemoteControlButtons.INFO)
    }

    fun text() {
        remoteCall(RemoteControlButtons.TEXT)
    }

    fun bouUP() {
        remoteCall(RemoteControlButtons.NEXT_BOUQUET)
    }

    fun bouDOWN() {
        remoteCall(RemoteControlButtons.PREVIOUS_BOUQUET)
    }


}