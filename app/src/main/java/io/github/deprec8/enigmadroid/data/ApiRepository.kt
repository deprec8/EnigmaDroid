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

package io.github.deprec8.enigmadroid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.ApiType
import io.github.deprec8.enigmadroid.data.enums.RemoteControlButtons
import io.github.deprec8.enigmadroid.data.objects.PreferencesKeys
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import io.github.deprec8.enigmadroid.model.api.Bookmark
import io.github.deprec8.enigmadroid.model.api.BookmarkList
import io.github.deprec8.enigmadroid.model.api.BouquetList
import io.github.deprec8.enigmadroid.model.api.CurrentInfo
import io.github.deprec8.enigmadroid.model.api.DeviceInfo
import io.github.deprec8.enigmadroid.model.api.EventList
import io.github.deprec8.enigmadroid.model.api.EventListList
import io.github.deprec8.enigmadroid.model.api.MovieList
import io.github.deprec8.enigmadroid.model.api.ServiceList
import io.github.deprec8.enigmadroid.model.api.SignalInfo
import io.github.deprec8.enigmadroid.model.api.Timer
import io.github.deprec8.enigmadroid.model.api.TimerList
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
    private val devicesDatabase: DevicesDatabase,
    private val dataStore: DataStore<Preferences>
) {

    private val currentDeviceKey = intPreferencesKey(PreferencesKeys.CURRENT_DEVICE)

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

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun makeOWIFURL(): String = withContext(Dispatchers.Default) {
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

    suspend fun buildLiveStreamURL(sRef: String): String = withContext(Dispatchers.Default) {
        getCurrentDevice()?.let { device ->
            buildString {
                append("http://")
                if (device.isLogin) {
                    append("${device.user}:${device.password}@")
                }
                append("${device.ip}:${device.livePort}/${sRef.replace(" ", "%20")}")
            }
        } ?: ""
    }

    suspend fun buildMovieStreamURL(file: String): String = withContext(Dispatchers.Default) {
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

    suspend fun fetchCurrentEventInfo(): CurrentInfo {
        return try {
            json.decodeFromString(
                CurrentInfo.serializer(),
                networkDataSource.fetchJson("getcurrent")
            )
        } catch (_: Exception) {

            CurrentInfo()
        }
    }

    suspend fun fetchEpgEvents(bRef: String): EventListList {
        return try {
            val epgEventList = json.decodeFromString(
                EventList.serializer(), networkDataSource.fetchJson(
                    "epgmulti?bRef=${
                        bRef.replace(
                            "\\\"", "\""
                        )
                    }&endTime=10080"
                )
            )

            val epgEventListList =
                EventListList(eventLists = epgEventList.events.groupBy { it.serviceName }
                    .map { (serviceName, events) ->
                        EventList(
                            name = serviceName, events = events
                        )
                    }, result = epgEventList.result)


            epgEventListList
        } catch (_: Exception) {
            EventListList()
        }
    }

    suspend fun fetchServiceEPG(sRef: String): EventList {
        return try {
            json.decodeFromString(
                EventList.serializer(),
                networkDataSource.fetchJson("epgservice?sRef=${sRef}&endTime=10080")
            )
        } catch (_: Exception) {
            EventList()
        }
    }

    fun fetchMovies(): Flow<MovieList> = flow {
        try {
            val bookmarks = mutableListOf<Bookmark>()
            val movies = json.decodeFromString(
                BookmarkList.serializer(),
                networkDataSource.fetchJson("movielist")
            )
            bookmarks.add(
                Bookmark(
                    directory = movies.directory,
                    displayName = "/"
                )
            )
            movies.bookmarks.forEach {
                bookmarks.add(
                    Bookmark(
                        directory = bookmarks[0].directory + it,
                        displayName = "/$it"
                    )
                )
            }
            bookmarks.forEach { bookmark ->
                val movieList = json.decodeFromString(
                    MovieList.serializer(),
                    networkDataSource.fetchJson(
                        "movielist?dirname=${
                            bookmark.directory
                        }"
                    )
                )
                emit(movieList.copy(bookmark = bookmark))
            }
        } catch (_: Exception) {

            emitAll(emptyList<MovieList>().asFlow())
        }
    }

    suspend fun renameMovie(sRef: String, newname: String) {
        networkDataSource.call("movierename?sRef=$sRef&newname=$newname")
    }

    suspend fun moveMovie(sRef: String, dirname: String) {
        networkDataSource.call("moviemove?sRef=$sRef&dirname=/media/hdd/movie/$dirname")
    }

    suspend fun deleteMovie(sRef: String) {
        networkDataSource.call("moviedelete?sRef=$sRef")
    }

    suspend fun fetchTimerServices(): List<ServiceList> {
        return try {
            val temp = mutableListOf<ServiceList>()
            json.decodeFromString(
                BouquetList.serializer(),
                networkDataSource.fetchJson("bouquets?stype=tv")
            ).bouquets.forEach { bouquet ->
                val nbRef = bouquet[0].replace("\\\"", "\"")
                temp.add(
                    json.decodeFromString(
                        ServiceList.serializer(),
                        networkDataSource.fetchJson("getallservices?sRef=$nbRef")
                    )
                )
            }
            temp
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun fetchEvents(type: ApiType): Flow<EventList> =
        flow {
            try {
                fetchBouquets(type).forEach { bouquet ->
                    val nbRef = bouquet[0].replace("\\\"", "\"")
                    val eventList = json.decodeFromString(
                        EventList.serializer(),
                        networkDataSource.fetchJson("epgnow?bRef=$nbRef")
                    )
                    emit(eventList.copy(name = bouquet[1]))
                }
            } catch (_: Exception) {

                emitAll(emptyList<EventList>().asFlow())
            }
        }

    suspend fun fetchBouquets(type: ApiType): List<List<String>> {
        return try {
            val bouquets = mutableListOf<List<String>>()
            val response = json.decodeFromString(
                BouquetList.serializer(),
                networkDataSource.fetchJson("bouquets?stype=${if (type == ApiType.TV) "tv" else "radio"}")
            ).bouquets
            for (bouquet in response) {
                bouquets.add(bouquet)
            }
            bouquets.add(
                listOf(
                    if (type == ApiType.TV) {
                        "1:7:1:0:0:0:0:0:0:0:(type%20==%201)%20||%20(type%20==%2017)%20||%20(type%20==%20195)%20||%20(type%20==%2025)%20ORDER%20BY%20name"
                    } else {
                        "1:7:2:0:0:0:0:0:0:0:(type%20==%202)%20ORDER%20BY%20name"
                    }, context.getString(R.string.all_services)
                )
            )
            val providers = if (type == ApiType.TV) {
                json.decodeFromString(
                    EventList.serializer(),
                    networkDataSource.fetchJson("epgnow?bRef=1:7:1:0:0:0:0:0:0:0:(type%20==%201)%20||%20(type%20==%2017)%20||%20(type%20==%20195)%20||%20(type%20==%2025)%20FROM%20PROVIDERS%20ORDER%20BY%20name")
                ).events
            } else {
                json.decodeFromString(
                    EventList.serializer(),
                    networkDataSource.fetchJson("epgnow?bRef=1:7:2:0:0:0:0:0:0:0:(type%20==%202)%20FROM%20PROVIDERS%20ORDER%20BY%20name")
                ).events
            }
            for (provider in providers) {
                bouquets.add(listOf(provider.serviceReference, provider.serviceName))
            }
            bouquets
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun play(sRef: String) {
        networkDataSource.call("zap?sRef=$sRef")
    }

    suspend fun fetchDeviceInfo(): DeviceInfo {
        return try {
            json.decodeFromString(
                DeviceInfo.serializer(),
                networkDataSource.fetchJson("deviceinfo")
            )

        } catch (_: Exception) {

            DeviceInfo()
        }
    }

    suspend fun fetchSignalInfo(): SignalInfo {
        return try {
            json.decodeFromString(
                SignalInfo.serializer(),
                networkDataSource.fetchJson("tunersignal")
            )
        } catch (_: Exception) {

            SignalInfo()
        }
    }

    suspend fun addTimer(timer: Timer) {
        networkDataSource.call("timeradd?sRef=${timer.serviceReference}&begin=${timer.beginTimestamp}&end=${timer.endTimestamp}&name=${timer.title}&disabled=${timer.disabled}&justplay=${timer.justPlay}&afterevent=${timer.afterEvent}&repeated=${timer.repeated}&description=${timer.shortDescription}&always_zap=${timer.alwaysZap}")
    }

    suspend fun addTimerForEvent(serviceReference: String, eventId: Int) {
        networkDataSource.call("timeraddbyeventid?sRef=${serviceReference}&eventid=${eventId}")
    }

    suspend fun editTimer(oldTimer: Timer, newTimer: Timer) {
        networkDataSource.call("timerchange?sRef=${newTimer.serviceReference}&begin=${newTimer.beginTimestamp}&end=${newTimer.endTimestamp}&name=${newTimer.title}&channelOld=${oldTimer.serviceReference}&beginOld=${oldTimer.beginTimestamp}&endOld=${oldTimer.endTimestamp}&disabled=${newTimer.disabled}&justplay=${newTimer.justPlay}&afterevent=${newTimer.afterEvent}&dirname=${oldTimer.directoryName}&tags=${oldTimer.tags}&repeated=${newTimer.repeated}&description=${newTimer.shortDescription}&always_zap=${newTimer.alwaysZap}")
    }

    suspend fun deleteTimer(timer: Timer) {
        networkDataSource.call("timerdelete?sRef=${timer.serviceReference}&begin=${timer.beginTimestamp}&end=${timer.endTimestamp}")
    }

    suspend fun fetchTimerList(): TimerList {
        return try {
            json.decodeFromString(
                TimerList.serializer(),
                networkDataSource.fetchJson("timerlist")
            )
        } catch (_: Exception) {
            TimerList()
        }
    }

    suspend fun remoteControlCall(button: RemoteControlButtons) {
        networkDataSource.remoteControlCall(button)
    }

    suspend fun setPowerState(command: Int) {
        networkDataSource.call("powerstate?newstate=$command")
    }
}