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

import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.data.repositories.DevicesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.inject

abstract class ContentViewModel(
    connectedDeviceId: Int? = null
) : ConnectionViewModel() {

    private val devicesRepository: DevicesRepository by inject()

    var connectedDeviceId = connectedDeviceId
        private set

    protected var fetchJob: Job? = null

    fun fetchData(forced: Boolean) {
        viewModelScope.launch {
            val currentDeviceId = devicesRepository.getCurrentDeviceId().firstOrNull()
            if (currentDeviceId != connectedDeviceId || forced) {
                onClearData()
                connectedDeviceId = currentDeviceId
            }

            if (shouldGetData()) {
                fetchJob?.cancel()
                fetchJob = launch {
                    onGetData()
                }
            }
        }
    }

    protected abstract fun onClearData()

    protected abstract suspend fun onGetData()

    protected abstract fun shouldGetData(): Boolean
}