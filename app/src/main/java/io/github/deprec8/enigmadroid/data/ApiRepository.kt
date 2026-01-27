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

package io.github.deprec8.enigmadroid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.ApiType
import io.github.deprec8.enigmadroid.data.enums.RemoteControlButtonType
import io.github.deprec8.enigmadroid.data.enums.RemoteControlPowerButtonType
import io.github.deprec8.enigmadroid.data.objects.PreferenceKey
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DeviceDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import io.github.deprec8.enigmadroid.model.api.BouquetBatch
import io.github.deprec8.enigmadroid.model.api.SignalInfo
import io.github.deprec8.enigmadroid.model.api.current.CurrentInfo
import io.github.deprec8.enigmadroid.model.api.device.DeviceInfo
import io.github.deprec8.enigmadroid.model.api.events.EventBatch
import io.github.deprec8.enigmadroid.model.api.events.EventBatchSet
import io.github.deprec8.enigmadroid.model.api.movies.MovieBatch
import io.github.deprec8.enigmadroid.model.api.movies.bookmarks.Bookmark
import io.github.deprec8.enigmadroid.model.api.movies.bookmarks.BookmarkBatch
import io.github.deprec8.enigmadroid.model.api.timers.Timer
import io.github.deprec8.enigmadroid.model.api.timers.TimerBatch
import io.github.deprec8.enigmadroid.model.api.timers.services.ServiceBatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val context: Context,
    private val networkDataSource: NetworkDataSource,
    private val deviceDatabase: DeviceDatabase,
    private val dataStore: DataStore<Preferences>
) {

    private val currentDeviceKey = intPreferencesKey(PreferenceKey.CURRENT_DEVICE)

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

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun buildOwifUrl(): String = withContext(Dispatchers.Default) {
        getCurrentDevice()?.let { device ->
            buildString {
                append(if (device.isHttps) "https://" else "http://")
                if (device.isLogin) {
                    append("${device.user}:${device.password}@")
                }
                append("${device.ip}:${device.port}")
            }
        } ?: ""
    }

    suspend fun buildLiveStreamUrl(serviceReference: String): String =
        withContext(Dispatchers.Default) {
            getCurrentDevice()?.let { device ->
                buildString {
                    append("http://")
                    if (device.isLogin) {
                        append("${device.user}:${device.password}@")
                    }
                    append(
                        "${device.ip}:${device.livePort}/${
                            serviceReference.replace(
                                " ", "%20"
                            )
                        }"
                    )
                }
            } ?: ""
        }

    suspend fun buildMovieStreamUrl(file: String): String = withContext(Dispatchers.Default) {
        getCurrentDevice()?.let { device ->
            buildString {
                if (device.isHttps) {
                    append("https://")
                } else {
                    append("http://")
                }
                if (device.isLogin) {
                    append("${device.user}:${device.password}@")
                }
                append("${device.ip}:${device.port}/file?file=${file.replace(" ", "%20")}")
            }
        } ?: ""
    }

    suspend fun fetchCurrentInfo(): CurrentInfo {
        return try {
            json.decodeFromString(
                CurrentInfo.serializer(), networkDataSource.fetchApi("getcurrent")
            )
        } catch (_: Exception) {

            CurrentInfo()
        }
    }

    suspend fun fetchEpgEventBatchSet(bouquetReference: String): EventBatchSet {
        return try {
            val epgEventBatch = json.decodeFromString(
                EventBatch.serializer(), networkDataSource.fetchApi(
                    "epgmulti?bRef=${
                        bouquetReference.replace(
                            "\\\"", "\""
                        )
                    }&endTime=10080"
                )
            )

            EventBatchSet(eventBatches = epgEventBatch.events.groupBy { it.serviceName }
                .map { (serviceName, events) ->
                    EventBatch(
                        name = serviceName, events = events
                    )
                }, result = epgEventBatch.result)

        } catch (_: Exception) {
            EventBatchSet()
        }
    }

    suspend fun fetchServiceEpgBatch(serviceReference: String): EventBatch {
        return try {
            json.decodeFromString(
                EventBatch.serializer(),
                networkDataSource.fetchApi("epgservice?sRef=${serviceReference}&endTime=10080")
            )
        } catch (_: Exception) {
            EventBatch()
        }
    }

    fun fetchMovieBatches(): Flow<MovieBatch> = flow {
        try {
            val bookmarks = mutableListOf<Bookmark>()
            val movies = json.decodeFromString(
                BookmarkBatch.serializer(), networkDataSource.fetchApi("movielist")
            )
            bookmarks.add(
                Bookmark(
                    directory = movies.directory, displayName = "/"
                )
            )
            movies.bookmarks.forEach {
                bookmarks.add(
                    Bookmark(
                        directory = bookmarks[0].directory + it, displayName = "/$it"
                    )
                )
            }
            bookmarks.forEach { bookmark ->
                val movieBatch = json.decodeFromString(
                    MovieBatch.serializer(), networkDataSource.fetchApi(
                        "movielist?dirname=${
                            bookmark.directory
                        }"
                    )
                )
                emit(movieBatch.copy(bookmark = bookmark))
            }
        } catch (_: Exception) {

            emitAll(emptyList<MovieBatch>().asFlow())
        }
    }

    suspend fun renameMovie(serviceReference: String, newName: String) {
        networkDataSource.postApi("movierename?sRef=$serviceReference&newname=$newName")
    }

    suspend fun moveMovie(serviceReference: String, dirName: String) {
        networkDataSource.postApi("moviemove?sRef=$serviceReference&dirname=/media/hdd/movie/$dirName")
    }

    suspend fun deleteMovie(serviceReference: String) {
        networkDataSource.postApi("moviedelete?sRef=$serviceReference")
    }

    suspend fun fetchTimerServiceBatches(): List<ServiceBatch> {
        return try {
            val serviceBatches = mutableListOf<ServiceBatch>()
            json.decodeFromString(
                BouquetBatch.serializer(), networkDataSource.fetchApi("bouquets?stype=tv")
            ).bouquets.forEach { bouquet ->
                val newBouquetReference = bouquet[0].replace("\\\"", "\"")
                serviceBatches.add(
                    json.decodeFromString(
                        ServiceBatch.serializer(),
                        networkDataSource.fetchApi("getallservices?sRef=$newBouquetReference")
                    )
                )
            }
            serviceBatches
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun fetchEventBatches(apiType: ApiType): Flow<EventBatch> = flow {
        try {
            fetchBouquets(apiType).forEach { bouquet ->
                val newBouquetReference = bouquet[0].replace("\\\"", "\"")
                val eventBatch = json.decodeFromString(
                    EventBatch.serializer(),
                    networkDataSource.fetchApi("epgnow?bRef=$newBouquetReference")
                )
                emit(eventBatch.copy(name = bouquet[1]))
            }
        } catch (_: Exception) {
            emitAll(emptyList<EventBatch>().asFlow())
        }
    }

    suspend fun fetchBouquets(apiType: ApiType): List<List<String>> {
        return try {
            val bouquets = mutableListOf<List<String>>()
            json.decodeFromString(
                BouquetBatch.serializer(),
                networkDataSource.fetchApi("bouquets?stype=${if (apiType == ApiType.TV) "tv" else "radio"}")
            ).bouquets.forEach { bouquet ->
                bouquets.add(bouquet)
            }
            bouquets.add(
                listOf(
                    if (apiType == ApiType.TV) {
                        "1:7:1:0:0:0:0:0:0:0:(type%20==%201)%20||%20(type%20==%2017)%20||%20(type%20==%20195)%20||%20(type%20==%2025)%20ORDER%20BY%20name"
                    } else {
                        "1:7:2:0:0:0:0:0:0:0:(type%20==%202)%20ORDER%20BY%20name"
                    }, context.getString(R.string.all_services)
                )
            )
            if (apiType == ApiType.TV) {
                json.decodeFromString(
                    EventBatch.serializer(),
                    networkDataSource.fetchApi("epgnow?bRef=1:7:1:0:0:0:0:0:0:0:(type%20==%201)%20||%20(type%20==%2017)%20||%20(type%20==%20195)%20||%20(type%20==%2025)%20FROM%20PROVIDERS%20ORDER%20BY%20name")
                ).events
            } else {
                json.decodeFromString(
                    EventBatch.serializer(),
                    networkDataSource.fetchApi("epgnow?bRef=1:7:2:0:0:0:0:0:0:0:(type%20==%202)%20FROM%20PROVIDERS%20ORDER%20BY%20name")
                ).events
            }.forEach { provider ->
                bouquets.add(listOf(provider.serviceReference, provider.serviceName))
            }
            bouquets
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun playOnDevice(serviceReference: String) {
        networkDataSource.postApi("zap?sRef=$serviceReference")
    }

    suspend fun fetchDeviceInfo(): DeviceInfo {
        return try {
            json.decodeFromString(
                DeviceInfo.serializer(), networkDataSource.fetchApi("deviceinfo")
            )

        } catch (_: Exception) {

            DeviceInfo()
        }
    }

    suspend fun fetchSignalInfo(): SignalInfo {
        return try {
            json.decodeFromString(
                SignalInfo.serializer(), networkDataSource.fetchApi("tunersignal")
            )
        } catch (_: Exception) {

            SignalInfo()
        }
    }

    suspend fun addTimer(timer: Timer) {
        networkDataSource.postApi("timeradd?sRef=${timer.serviceReference}&begin=${timer.beginTimestamp}&end=${timer.endTimestamp}&name=${timer.title}&disabled=${timer.disabled}&justplay=${timer.justPlay}&afterevent=${timer.afterEvent}&repeated=${timer.repeated}&description=${timer.shortDescription}&always_zap=${timer.alwaysZap}")
    }

    suspend fun addTimerForEvent(serviceReference: String, eventId: Int) {
        networkDataSource.postApi("timeraddbyeventid?sRef=${serviceReference}&eventid=${eventId}")
    }

    suspend fun editTimer(oldTimer: Timer, newTimer: Timer) {
        networkDataSource.postApi("timerchange?sRef=${newTimer.serviceReference}&begin=${newTimer.beginTimestamp}&end=${newTimer.endTimestamp}&name=${newTimer.title}&channelOld=${oldTimer.serviceReference}&beginOld=${oldTimer.beginTimestamp}&endOld=${oldTimer.endTimestamp}&disabled=${newTimer.disabled}&justplay=${newTimer.justPlay}&afterevent=${newTimer.afterEvent}&dirname=${oldTimer.directoryName}&tags=${oldTimer.tags}&repeated=${newTimer.repeated}&description=${newTimer.shortDescription}&always_zap=${newTimer.alwaysZap}")
    }

    suspend fun deleteTimer(timer: Timer) {
        networkDataSource.postApi("timerdelete?sRef=${timer.serviceReference}&begin=${timer.beginTimestamp}&end=${timer.endTimestamp}")
    }

    suspend fun fetchTimerBatch(): TimerBatch {
        return try {
            json.decodeFromString(
                TimerBatch.serializer(), networkDataSource.fetchApi("timerlist")
            )
        } catch (_: Exception) {
            TimerBatch()
        }
    }

    suspend fun remoteControlCall(type: RemoteControlButtonType) {
        networkDataSource.postApi(type)
    }

    suspend fun setPowerState(type: RemoteControlPowerButtonType) {
        networkDataSource.postApi("powerstate?newstate=${type.id}")
    }
}