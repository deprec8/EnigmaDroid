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
import io.github.deprec8.enigmadroid.data.ConnectionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

abstract class ContentViewModel : ConnectionViewModel() {

    private var isActive = false
    private var isDirty = false
    private var lastFetchedAt = 0L
    private val staleThresholdMs = 300_000L

    protected var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            connectionState.filter { it == ConnectionState.CONNECTED }.collectLatest {
                if (isActive) {
                    fetchData(showLoading = true)
                } else {
                    isDirty = true
                }
            }
        }
    }

    fun fetchData(showLoading: Boolean = true) {
        if (!showLoading && (fetchJob?.isActive == true)) return
        viewModelScope.launch {
            fetchJob?.cancel()
            fetchJob = launch {
                if (showLoading) onClearData()
                onGetData()
                if (connectionState.value == ConnectionState.CONNECTED) {
                    lastFetchedAt = System.currentTimeMillis()
                }
            }
        }
    }


    fun onActive() {
        isActive = true
        if (connectionState.value != ConnectionState.CONNECTED) {
            checkConnection()
        }
        val stale = (System.currentTimeMillis() - lastFetchedAt) > staleThresholdMs
        if (isDirty || stale || lastFetchedAt == 0L) {
            fetchData(showLoading = isDirty || lastFetchedAt == 0L)
        }
        isDirty = false
    }

    fun onInactive() {
        isActive = false
    }

    protected abstract fun onClearData()

    protected abstract suspend fun onGetData()
}