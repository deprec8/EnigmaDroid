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

package io.github.deprec8.enigmadroid.data.source.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.data.enums.RemoteControlButtonType
import io.github.deprec8.enigmadroid.data.objects.PreferenceKey
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DeviceDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.ClosedByteChannelException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.ConnectionPool
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val deviceDatabase: DeviceDatabase,
    private val context: Context
) {

    private val currentDeviceKey = intPreferencesKey(PreferenceKey.CURRENT_DEVICE)
    private val loadingStateKey = intPreferencesKey(PreferenceKey.LOADING_STATE)

    private suspend fun getCurrentDevice(): Device? {
        val listId = dataStore.data.map { preferences ->
            preferences[currentDeviceKey]
        }.firstOrNull()
        val allDevices = deviceDatabase.deviceDao().getAll().firstOrNull()
        return if (allDevices.isNullOrEmpty()) {
            null
        } else {
            allDevices[listId ?: 0]
        }
    }

    private suspend fun updateLoadingState(exception: Exception) {
        when (exception) {
            is ClosedByteChannelException -> {
                dataStore.edit { preferences ->
                    preferences[loadingStateKey] = LoadingState.INVALID_DEVICE_RESPONSE.id
                }
            }
            else                          -> {
                val currentLoadingState = dataStore.data.map { preferences ->
                    LoadingState.entries[preferences[loadingStateKey] ?: 3]
                }.first()

                if (currentLoadingState != LoadingState.LOADING) {
                    dataStore.edit { preferences ->
                        preferences[loadingStateKey] = LoadingState.LOADING.id
                    }
                }

                if (deviceDatabase.deviceDao().getAll().firstOrNull().isNullOrEmpty().not()) {
                    if (isNetworkAvailable()) {
                        dataStore.edit { preferences ->
                            preferences[loadingStateKey] = LoadingState.DEVICE_NOT_ONLINE.id
                        }
                    } else {
                        dataStore.edit { preferences ->
                            preferences[loadingStateKey] = LoadingState.NO_NETWORK_AVAILABLE.id
                        }
                    }
                } else {
                    dataStore.edit { preferences ->
                        preferences[loadingStateKey] = LoadingState.NO_DEVICE_AVAILABLE.id
                    }
                }
            }
        }
    }

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 20000
            connectTimeoutMillis = 8000
            socketTimeoutMillis = 20000
        }
        engine {
            config {
                connectionPool(ConnectionPool(5, 1, TimeUnit.MINUTES))
            }
        }
    }

    private val checkClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 3000
        }
        engine {
            config {
                connectionPool(ConnectionPool(5, 1, TimeUnit.MINUTES))
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

    private suspend fun buildUrl(button: RemoteControlButtonType): String =
        withContext(Dispatchers.Default) {
            getCurrentDevice()?.let { device ->
                buildString {
                    append(if (device.isHttps) "https://" else "http://")

                    if (device.isLogin) {
                        append("${device.user}:${device.password}@")
                    }
                    append("${device.ip}:${device.port}/web/remotecontrol?command=${button.id}")
                }
            } ?: ""
        }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork

        return activeNetwork != null
    }

    suspend fun isDeviceOnline(): Boolean = try {
        checkClient.get(buildUrl("currenttime")) {
            header(HttpHeaders.Connection, "close")
        }
        true
    } catch (e: Exception) {
        updateLoadingState(e)
        false
    }

    suspend fun postApi(endpoint: String) {
        try {
            client.get(buildUrl(endpoint)) {
                header(HttpHeaders.Connection, "close")
            }
        } catch (e: Exception) {
            updateLoadingState(e)
        }
    }

    suspend fun postApi(button: RemoteControlButtonType) {
        try {
            client.get(buildUrl(button)) {
                header(HttpHeaders.Connection, "close")
            }
        } catch (e: Exception) {
            updateLoadingState(e)
        }
    }

    suspend fun fetchApi(endpoint: String): String = try {
        client.get(buildUrl(endpoint)) {
            header(HttpHeaders.Connection, "close")
        }.bodyAsText()
    } catch (e: Exception) {
        updateLoadingState(e)
        ""
    }
}