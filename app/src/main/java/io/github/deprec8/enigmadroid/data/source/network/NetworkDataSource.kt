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

package io.github.deprec8.enigmadroid.data.source.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.data.objects.LoadingState
import io.github.deprec8.enigmadroid.data.objects.PreferencesKeys
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val devicesDatabase: DevicesDatabase
) {

    private val currentDeviceKey = intPreferencesKey(PreferencesKeys.CURRENT_DEVICE)
    private val loadingStateKey = intPreferencesKey(PreferencesKeys.LOADING_STATE)

    private suspend fun getCurrentDevice(): Device? {
        val listId = dataStore.data.map { preferences ->
            preferences[currentDeviceKey]
        }.firstOrNull()
        val allDevices = devicesDatabase.deviceDao().getAll().firstOrNull()
        return if (allDevices.isNullOrEmpty()) {
            null
        } else {
            allDevices[listId ?: 0]
        }
    }

    private suspend fun updateLoadingState() {
        val currentLoadingState = dataStore.data.map { preferences ->
            LoadingState.entries[preferences[loadingStateKey] ?: 3]
        }.first()

        if (currentLoadingState != LoadingState.LOADING) {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.LOADING.ordinal
            }
        }

        if (devicesDatabase.deviceDao().getAll().firstOrNull().isNullOrEmpty().not()) {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.DEVICE_NOT_ONLINE.ordinal
            }
        } else {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.NO_DEVICE_AVAILABLE.ordinal
            }
        }
    }

    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 15000
        }
        engine {
            maxConnectionsCount = 100
            endpoint {
                keepAliveTime = 5000
                connectTimeout = 15000
                socketTimeout = 30000
            }
        }
    }

    private val checkClient = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 5000
        }
        engine {
            maxConnectionsCount = 30
            endpoint {
                keepAliveTime = 5000
                connectTimeout = 5000
                socketTimeout = 5000
            }
        }
    }

    private suspend fun buildUrl(endpoint: String): String = withContext(Dispatchers.Default) {
        getCurrentDevice()?.let { device ->
            buildString {
                append(if (device.isHttps) "https://" else "http://")
                if (device.isLogin) {
                    append("${device.user}:${device.password}@")
                }
                append("${device.ip}:${device.port}")
                append("/api/${endpoint.replace(" ", "%20")}")
            }
        }
    } ?: ""

    private suspend fun buildRemoteUrl(command: Int): String = withContext(Dispatchers.Default) {
        getCurrentDevice()?.let { device ->
            buildString {
                append(if (device.isHttps) "https://" else "http://")

                if (device.isLogin) {
                    append("${device.user}:${device.password}@")
                }
                append("${device.ip}:${device.port}/web/remotecontrol?command=$command")
            }
        } ?: ""
    }

    suspend fun isDeviceOnline(): Boolean = safeApiCall {
        checkClient.get(buildUrl("currenttime"))
        true
    } == true

    suspend fun remoteControlCall(command: Int) = safeApiCall {
        client.get(buildRemoteUrl(command = command))
    }

    suspend fun call(urlEnd: String) = safeApiCall {
        client.get(buildUrl(urlEnd))
    }

    suspend fun fetchJson(file: String): String = safeApiCall {
        client.get(buildUrl(file)) {
            header(HttpHeaders.Connection, "close")
        }.bodyAsText()
    } ?: ""

    private suspend inline fun <T> safeApiCall(
        crossinline block: suspend () -> T
    ): T? = withContext(Dispatchers.IO) {
        try {
            block()
        } catch (_: Exception) {
            updateLoadingState()
            null
        }
    }
}