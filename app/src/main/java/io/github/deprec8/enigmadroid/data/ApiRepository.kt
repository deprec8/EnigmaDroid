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
import io.github.deprec8.enigmadroid.data.enums.EntryType
import io.github.deprec8.enigmadroid.data.enums.RemoteControlButtonType
import io.github.deprec8.enigmadroid.data.enums.RemoteControlPowerButtonType
import io.github.deprec8.enigmadroid.data.objects.DefaultBouquet
import io.github.deprec8.enigmadroid.data.objects.PreferenceKey
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DeviceDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import io.github.deprec8.enigmadroid.model.api.Bouquet
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
import io.github.deprec8.enigmadroid.model.api.timers.services.ServiceBatchSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    private fun EntryType.shouldBeNumbered(): Boolean {
        return when (this) {
            EntryType.CHANNEL, EntryType.NUMBERED_MARKER, EntryType.INVISIBLE_NUMBERED_MARKER -> true
            else                                                                              -> false
        }
    }

    private fun String.toEntryType(): EntryType {
        val flag = split(":").getOrNull(1)?.toIntOrNull() ?: return EntryType.CHANNEL

        return EntryType.entries.firstOrNull { it.flag == flag } ?: EntryType.CHANNEL
    }

    suspend fun fetchCurrentInfo(): CurrentInfo {
        val rawJson = networkDataSource.fetchApi("getcurrent")

        return withContext(Dispatchers.Default) {
            runCatching {
                json.decodeFromString(
                    CurrentInfo.serializer(), rawJson
                )
            }.getOrDefault(CurrentInfo())
        }
    }

    suspend fun fetchEpgEventBatchSet(bouquetReference: String): EventBatchSet {
        val rawJson = networkDataSource.fetchApi(
            "epgmulti?bRef=${
                bouquetReference.replace(
                    "\\\"", "\""
                )
            }&endTime=10080"
        )

        return withContext(Dispatchers.Default) {
            runCatching {
                val epgEventBatch = json.decodeFromString(
                    EventBatch.serializer(), rawJson
                )

                EventBatchSet(eventBatches = epgEventBatch.events.groupBy { it.serviceName }
                    .map { (serviceName, events) ->
                        EventBatch(
                            name = serviceName, events = events
                        )
                    }, result = epgEventBatch.result)

            }.getOrDefault(EventBatchSet())
        }
    }

    suspend fun fetchServiceEpgBatch(serviceReference: String): EventBatch {
        val rawJson =
            networkDataSource.fetchApi("epgservice?sRef=${serviceReference}&endTime=10080")

        return withContext(Dispatchers.Default) {
            runCatching {
                json.decodeFromString(
                    EventBatch.serializer(), rawJson
                )
            }.getOrDefault(EventBatch())
        }
    }

    fun fetchMovieBatches(): Flow<MovieBatch> = flow {
        val bookmarks = mutableListOf<Bookmark>()
        val rawBookmarkJson = networkDataSource.fetchApi("movielist")
        val movies = json.decodeFromString(
            BookmarkBatch.serializer(), rawBookmarkJson
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
            val rawJson = networkDataSource.fetchApi(
                "movielist?dirname=${
                    bookmark.directory
                }"
            )
            val movieBatch = json.decodeFromString(
                MovieBatch.serializer(), rawJson
            )
            emit(movieBatch.copy(bookmark = bookmark))
        }
    }.flowOn(Dispatchers.Default).catch {
        emit(MovieBatch())
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

    suspend fun fetchServiceBatchSet(): ServiceBatchSet {
        val rawTvJson = networkDataSource.fetchApi("getallservices")
        val rawRadioJson = networkDataSource.fetchApi("getallservices?type=radio")

        return withContext(Dispatchers.Default) {
            runCatching {
                val tvServiceBatchSet = json.decodeFromString(
                    ServiceBatchSet.serializer(), rawTvJson
                )

                val radioServiceBatchSet = json.decodeFromString(
                    ServiceBatchSet.serializer(), rawRadioJson
                )

                val rawServiceBatches =
                    tvServiceBatchSet.serviceBatches + radioServiceBatchSet.serviceBatches

                val uiServiceBatches = mutableListOf<ServiceBatch>()

                rawServiceBatches.forEach { serviceBatch ->
                    var counter = 1

                    val uiServices = serviceBatch.services.map { service ->
                        val type = service.serviceReference.toEntryType()
                        val displayIndex = if (type.shouldBeNumbered()) counter ++ else null

                        service.copy(
                            displayIndex = displayIndex, type = type
                        )
                    }

                    uiServiceBatches.add(
                        serviceBatch.copy(
                            services = uiServices
                        )
                    )
                }
                ServiceBatchSet(
                    serviceBatches = uiServiceBatches,
                    result = tvServiceBatchSet.result || radioServiceBatchSet.result
                )
            }.getOrDefault(ServiceBatchSet())
        }
    }

    fun fetchEventBatches(apiType: ApiType): Flow<EventBatch> = flow {
        fetchBouquets(apiType).forEach { bouquet ->
            val newBouquetReference = bouquet.reference.replace("\\\"", "\"")

            val rawJson = networkDataSource.fetchApi("epgnow?bRef=$newBouquetReference")

            val rawBatch = json.decodeFromString(
                EventBatch.serializer(), rawJson
            )

            var counter = 1

            val uiEvents = rawBatch.events.map { event ->
                val type = event.serviceReference.toEntryType()

                val displayIndex = if (type.shouldBeNumbered()) counter ++ else null

                event.copy(
                    displayIndex = displayIndex, type = type
                )
            }

            emit(
                rawBatch.copy(
                    name = bouquet.name, events = uiEvents
                )
            )
        }

    }.flowOn(Dispatchers.Default).catch {
        emit(EventBatch())
    }

    suspend fun fetchBouquets(apiType: ApiType): List<Bouquet> {
        val rawUserJson =
            networkDataSource.fetchApi("bouquets?stype=${if (apiType == ApiType.TV) "tv" else "radio"}")
        val rawProviderJson = networkDataSource.fetchApi(
            if (apiType == ApiType.TV) {
                "epgnow?bRef=${DefaultBouquet.ALL_PROVIDERS_TV}"
            } else {
                "epgnow?bRef=${DefaultBouquet.ALL_PROVIDERS_RADIO}"
            }
        )

        return withContext(Dispatchers.Default) {
            runCatching {
                val bouquets = mutableListOf<Bouquet>()
                json.decodeFromString(
                    BouquetBatch.serializer(), rawUserJson
                ).bouquets.forEach { bouquet ->
                    if (bouquet[0].toEntryType() != EntryType.INVISIBLE_DIRECTORY) {
                        bouquets.add(
                            Bouquet(
                                reference = bouquet[0], name = bouquet[1]
                            )
                        )
                    }
                }
                bouquets.add(
                    Bouquet(
                        if (apiType == ApiType.TV) {
                            DefaultBouquet.ALL_SERVICES_TV
                        } else {
                            DefaultBouquet.ALL_SERVICES_RADIO
                        }, context.getString(R.string.all_services)
                    )
                )
                json.decodeFromString(
                    EventBatch.serializer(), rawProviderJson
                ).events.forEach { provider ->
                    if (provider.serviceReference.toEntryType() != EntryType.INVISIBLE_DIRECTORY) {
                        bouquets.add(
                            Bouquet(
                                provider.serviceReference, provider.serviceName
                            )
                        )
                    }
                }
                bouquets
            }.getOrDefault(emptyList())
        }
    }

    suspend fun playOnDevice(serviceReference: String) {
        networkDataSource.postApi("zap?sRef=$serviceReference")
    }

    suspend fun fetchDeviceInfo(): DeviceInfo {
        val rawJson = networkDataSource.fetchApi("deviceinfo")

        return withContext(Dispatchers.Default) {
            runCatching {
                json.decodeFromString(
                    DeviceInfo.serializer(), rawJson
                )
            }.getOrDefault(DeviceInfo())
        }
    }

    suspend fun fetchSignalInfo(): SignalInfo {
        val rawJson = networkDataSource.fetchApi("tunersignal")

        return withContext(Dispatchers.Default) {
            runCatching {
                json.decodeFromString(
                    SignalInfo.serializer(), rawJson
                )
            }.getOrDefault(SignalInfo())
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

    suspend fun toggleTimerStatus(timer: Timer) {
        networkDataSource.postApi("timertogglestatus?sRef=${timer.serviceReference}&begin=${timer.beginTimestamp}&end=${timer.endTimestamp}")
    }

    suspend fun fetchTimerBatch(): TimerBatch {
        val rawJson = networkDataSource.fetchApi("timerlist")

        return withContext(Dispatchers.Default) {
            runCatching {
                json.decodeFromString(
                    TimerBatch.serializer(), rawJson
                )
            }.getOrDefault(TimerBatch())
        }
    }

    suspend fun remoteControlCall(type: RemoteControlButtonType) {
        networkDataSource.postApi(type)
    }

    suspend fun setPowerState(type: RemoteControlPowerButtonType) {
        networkDataSource.postApi("powerstate?newstate=${type.id}")
    }
}