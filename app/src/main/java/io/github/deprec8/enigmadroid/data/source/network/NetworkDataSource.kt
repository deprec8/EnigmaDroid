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

import io.github.deprec8.enigmadroid.common.enums.RemoteControlKey
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.data.ConnectionStateHolder
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesLocalDataSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.ClosedByteChannelException
import okhttp3.ConnectionPool
import java.util.concurrent.TimeUnit

class NetworkDataSource(
    private val connectionStateHolder: ConnectionStateHolder,
    private val devicesLocalDataSource: DevicesLocalDataSource
) {
    private suspend fun handleException(e: Exception) {
        val hasDevices = devicesLocalDataSource.getCount() > 0

        when (e) {
            is CancellationException -> throw e

            is NullPointerException if hasDevices -> connectionStateHolder.updateConnectionState(
                ConnectionState.NO_DEVICE_SELECTED
            )

            is ClosedByteChannelException -> connectionStateHolder.updateConnectionState(
                ConnectionState.INVALID_DEVICE_RESPONSE
            )

            else -> if (!hasDevices) {
                connectionStateHolder.updateConnectionState(ConnectionState.NO_DEVICE_AVAILABLE)
            } else {
                connectionStateHolder.updateConnectionState(ConnectionState.NOT_CONNECTED)
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

    suspend fun checkConnection(forced: Boolean = true) {
        if (connectionStateHolder.connectionState.value == ConnectionState.CONNECTING || forced) {
            connectionStateHolder.updateConnectionState(ConnectionState.CONNECTING)
            try {
                val url = devicesLocalDataSource.getCurrentStatic()?.buildUrl("currenttime")
                    ?: throw NullPointerException()
                checkClient.get(url) {
                    header(HttpHeaders.Connection, "close")
                }
                connectionStateHolder.updateConnectionState(ConnectionState.CONNECTED)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    suspend fun post(endpoint: String) {
        try {
            val url = devicesLocalDataSource.getCurrentStatic()?.buildUrl(endpoint)
                ?: throw NullPointerException()
            client.get(url) {
                header(HttpHeaders.Connection, "close")
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun post(key: RemoteControlKey) {
        try {
            val url = devicesLocalDataSource.getCurrentStatic()?.buildUrl(key)
                ?: throw NullPointerException()
            client.get(url) {
                header(HttpHeaders.Connection, "close")
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun fetchJson(endpoint: String): String = try {
        val url = devicesLocalDataSource.getCurrentStatic()?.buildUrl(endpoint)
            ?: throw NullPointerException()
        client.get(url) {
            header(HttpHeaders.Connection, "close")
        }.bodyAsText()
    } catch (e: Exception) {
        handleException(e)
        ""
    }
}