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

package io.github.deprec8.enigmadroid.ui.current

import android.net.Uri
import io.github.deprec8.enigmadroid.data.repositories.ApiRepository
import io.github.deprec8.enigmadroid.model.api.CurrentInfo
import io.github.deprec8.enigmadroid.ui.components.viewmodels.ContentViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrentViewModel(
    private val apiRepository: ApiRepository
) : ContentViewModel() {

    private val _currentInfo = MutableStateFlow<CurrentInfo?>(null)
    val currentInfo: StateFlow<CurrentInfo?> = _currentInfo.asStateFlow()

    suspend fun buildLiveStreamUri(serviceReference: String): Uri? {
        return apiRepository.buildLiveStreamUri(serviceReference)
    }

    override fun onClearData() {
        _currentInfo.value = null
    }

    override suspend fun onGetData() {
        _currentInfo.value = apiRepository.fetchCurrentInfo()
    }
}