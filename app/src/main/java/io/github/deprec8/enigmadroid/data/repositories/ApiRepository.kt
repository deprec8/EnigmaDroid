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

package io.github.deprec8.enigmadroid.data.repositories

import android.content.Context
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.ContentFlag
import io.github.deprec8.enigmadroid.common.enums.ContentType
import io.github.deprec8.enigmadroid.common.enums.RemoteControlKey
import io.github.deprec8.enigmadroid.common.enums.RemoteControlPowerKey
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesLocalDataSource
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import io.github.deprec8.enigmadroid.model.api.Bouquet
import io.github.deprec8.enigmadroid.model.api.BouquetBatch
import io.github.deprec8.enigmadroid.model.api.CurrentInfo
import io.github.deprec8.enigmadroid.model.api.DeviceInfo
import io.github.deprec8.enigmadroid.model.api.EventBatch
import io.github.deprec8.enigmadroid.model.api.EventBatchSet
import io.github.deprec8.enigmadroid.model.api.MovieBatch
import io.github.deprec8.enigmadroid.model.api.ServiceBatchSet
import io.github.deprec8.enigmadroid.model.api.SignalInfo
import io.github.deprec8.enigmadroid.model.api.Timer
import io.github.deprec8.enigmadroid.model.api.TimerBatch
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

class ApiRepository(
    private val context: Context,
    private val networkDataSource: NetworkDataSource,
    private val devicesLocalDataSource: DevicesLocalDataSource
) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun buildLiveStreamUri(serviceReference: String) =
        devicesLocalDataSource.getCurrentStatic()?.buildLiveStreamUri(serviceReference)

    suspend fun buildMovieStreamUri(file: String) =
        devicesLocalDataSource.getCurrentStatic()?.buildMovieStreamUri(file)

    private suspend fun <T> fetchDecoded(
        serializer: KSerializer<T>, urlBuilder: URLBuilder.() -> Unit
    ): Result<T> = networkDataSource.get(urlBuilder).mapCatching { raw ->
        withContext(Dispatchers.Default) {
            json.decodeFromString(serializer, raw)
        }
    }.onFailure { exception ->
        if (exception is CancellationException) throw exception
    }

    private fun ContentFlag.shouldBeNumbered(): Boolean {
        return when (this) {
            ContentFlag.Channel, ContentFlag.NumberedMarker, ContentFlag.InvisibleNumberedMarker -> true
            else -> false
        }
    }

    private fun String.toContentFlag(): ContentFlag {
        val flag = split(":").getOrNull(1)?.toIntOrNull() ?: return ContentFlag.Channel

        return ContentFlag.entries.firstOrNull { it.flag == flag } ?: ContentFlag.Channel
    }

    suspend fun fetchCurrentInfo() = fetchDecoded(CurrentInfo.serializer()) {
        appendPathSegments("api", "getcurrent")
    }

    suspend fun fetchEpgEventBatchSet(bouquetReference: String) =
        fetchDecoded(EventBatch.serializer()) {
            appendPathSegments("api", "epgmulti")
            parameters.append("bRef", bouquetReference)
            parameters.append("endTime", "10080")
        }.map { batch ->
            EventBatchSet(eventBatches = batch.events.groupBy { it.serviceName }
                .map { (serviceName, events) -> EventBatch(name = serviceName, events = events) })
        }

    suspend fun fetchServiceEpgBatch(serviceReference: String) =
        fetchDecoded(EventBatch.serializer()) {
            appendPathSegments("api", "epgservice")
            parameters.append("sRef", serviceReference)
            parameters.append("endTime", "10080")
        }

    suspend fun fetchMovieBatch(directory: String? = null) = fetchDecoded(MovieBatch.serializer()) {
        appendPathSegments("api", "movielist")
        directory?.let { parameters.append("dirname", it) }
    }

    suspend fun fetchFreeSpace(directory: String) = fetchDecoded(DeviceInfo.serializer()) {
        appendPathSegments("api", "deviceinfo")
    }.mapCatching { deviceInfo ->
        deviceInfo.hdds.firstOrNull { directory.startsWith(it.mountDirectory) }?.freeSpace
            ?: throw NullPointerException()
    }

    suspend fun renameMovie(serviceReference: String, newName: String) {
        networkDataSource.post {
            appendPathSegments("api", "movierename")
            parameters.append("sRef", serviceReference)
            parameters.append("newname", newName)
        }
    }

    suspend fun moveMovie(serviceReference: String, dirName: String) {
        networkDataSource.post {
            appendPathSegments("api", "moviemove")
            parameters.append("sRef", serviceReference)
            parameters.append("dirname", dirName)
        }
    }

    suspend fun deleteMovie(serviceReference: String) {
        networkDataSource.post {
            appendPathSegments("api", "moviedelete")
            parameters.append("sRef", serviceReference)
        }
    }

    suspend fun fetchServiceBatchSet(): Result<ServiceBatchSet> {
        val tvResult = networkDataSource.get {
            appendPathSegments("api", "getallservices")
        }
        val radioResult = networkDataSource.get {
            appendPathSegments("api", "getallservices")
            parameters.append("type", "radio")
        }

        return withContext(Dispatchers.Default) {
            runCatching {
                val tvServiceBatchSet = json.decodeFromString(
                    ServiceBatchSet.serializer(), tvResult.getOrThrow()
                )
                val radioServiceBatchSet = json.decodeFromString(
                    ServiceBatchSet.serializer(), radioResult.getOrThrow()
                )

                val rawServiceBatches =
                    tvServiceBatchSet.serviceBatches + radioServiceBatchSet.serviceBatches

                val uiServiceBatches = rawServiceBatches.map { serviceBatch ->
                    var counter = 1
                    serviceBatch.copy(services = serviceBatch.services.map { service ->
                        val flag = service.serviceReference.toContentFlag()
                        val displayIndex = if (flag.shouldBeNumbered()) counter++ else null
                        service.copy(displayIndex = displayIndex, flag = flag)
                    })
                }

                ServiceBatchSet(serviceBatches = uiServiceBatches)
            }
        }
    }

    fun fetchEventBatches(type: ContentType): Flow<Result<EventBatch>> = flow {
        fetchBouquets(type).map { bouquets ->
            bouquets.forEach { bouquet ->
                emit(fetchDecoded(EventBatch.serializer()) {
                    appendPathSegments("api", "epgnow")
                    parameters.append("bRef", bouquet.reference)
                }.map { batch ->
                    var counter = 1
                    val uiEvents = batch.events.map { event ->
                        val flag = event.serviceReference.toContentFlag()
                        val displayIndex = if (flag.shouldBeNumbered()) counter++ else null
                        event.copy(displayIndex = displayIndex, flag = flag)
                    }
                    batch.copy(name = bouquet.name, events = uiEvents)
                })
            }
        }
    }

    suspend fun fetchBouquets(type: ContentType): Result<List<Bouquet>> {
        val userResult = networkDataSource.get {
            appendPathSegments("api", "bouquets")
            parameters.append(
                "stype", if (type == ContentType.Tv || type == ContentType.TvEpg) "tv" else "radio"
            )
        }
        val providerResult = networkDataSource.get {
            appendPathSegments("api", "epgnow")
            parameters.append(
                "bRef",
                if (type == ContentType.Tv || type == ContentType.TvEpg) ALL_PROVIDERS_TV else ALL_PROVIDERS_RADIO
            )
        }

        return withContext(Dispatchers.Default) {
            runCatching {
                val bouquets = mutableListOf<Bouquet>()
                json.decodeFromString(
                    BouquetBatch.serializer(), userResult.getOrThrow()
                ).bouquets.forEach { bouquet ->
                    if (bouquet[0].toContentFlag() != ContentFlag.InvisibleDirectory) {
                        bouquets.add(Bouquet(reference = bouquet[0], name = bouquet[1]))
                    }
                }
                bouquets.add(
                    Bouquet(
                        if (type == ContentType.Tv) ALL_SERVICES_TV else ALL_SERVICES_RADIO,
                        context.getString(R.string.all_services)
                    )
                )
                json.decodeFromString(
                    EventBatch.serializer(), providerResult.getOrThrow()
                ).events.forEach { provider ->
                    if (provider.serviceReference.toContentFlag() != ContentFlag.InvisibleDirectory) {
                        bouquets.add(Bouquet(provider.serviceReference, provider.serviceName))
                    }
                }
                bouquets
            }
        }
    }

    suspend fun playOnDevice(serviceReference: String) {
        networkDataSource.post {
            appendPathSegments("api", "zap")
            parameters.append("sRef", serviceReference)
        }
    }

    suspend fun fetchDeviceInfo() = fetchDecoded(DeviceInfo.serializer()) {
        appendPathSegments("api", "deviceinfo")
    }

    suspend fun fetchSignalInfo() = fetchDecoded(SignalInfo.serializer()) {
        appendPathSegments("api", "tunersignal")
    }

    suspend fun addTimer(timer: Timer) {
        networkDataSource.post {
            appendPathSegments("api", "timeradd")
            parameters.append("sRef", timer.serviceReference)
            parameters.append("begin", timer.beginTimestamp.toString())
            parameters.append("end", timer.endTimestamp.toString())
            parameters.append("name", timer.title)
            parameters.append("disabled", timer.disabled.toString())
            parameters.append("justplay", timer.justPlay.toString())
            parameters.append("afterevent", timer.afterEvent.toString())
            parameters.append("repeated", timer.repeated.toString())
            parameters.append("description", timer.shortDescription)
            parameters.append("always_zap", timer.alwaysZap.toString())
        }
    }

    suspend fun addTimerForEvent(serviceReference: String, eventId: Int) {
        networkDataSource.post {
            appendPathSegments("api", "timeraddbyeventid")
            parameters.append("sRef", serviceReference)
            parameters.append("eventid", eventId.toString())
        }
    }

    suspend fun editTimer(oldTimer: Timer, newTimer: Timer) {
        networkDataSource.post {
            appendPathSegments("api", "timerchange")
            parameters.append("sRef", newTimer.serviceReference)
            parameters.append("begin", newTimer.beginTimestamp.toString())
            parameters.append("end", newTimer.endTimestamp.toString())
            parameters.append("name", newTimer.title)
            parameters.append("channelOld", oldTimer.serviceReference)
            parameters.append("beginOld", oldTimer.beginTimestamp.toString())
            parameters.append("endOld", oldTimer.endTimestamp.toString())
            parameters.append("disabled", newTimer.disabled.toString())
            parameters.append("justplay", newTimer.justPlay.toString())
            parameters.append("afterevent", newTimer.afterEvent.toString())
            parameters.append("dirname", oldTimer.directoryName)
            parameters.append("tags", oldTimer.tags)
            parameters.append("repeated", newTimer.repeated.toString())
            parameters.append("description", newTimer.shortDescription)
            parameters.append("always_zap", newTimer.alwaysZap.toString())
        }
    }

    suspend fun deleteTimer(timer: Timer) {
        networkDataSource.post {
            appendPathSegments("api", "timerdelete")
            parameters.append("sRef", timer.serviceReference)
            parameters.append("begin", timer.beginTimestamp.toString())
            parameters.append("end", timer.endTimestamp.toString())
        }
    }

    suspend fun toggleTimerStatus(timer: Timer) {
        networkDataSource.post {
            appendPathSegments("api", "timertogglestatus")
            parameters.append("sRef", timer.serviceReference)
            parameters.append("begin", timer.beginTimestamp.toString())
            parameters.append("end", timer.endTimestamp.toString())
        }
    }

    suspend fun fetchTimerBatch() = fetchDecoded(TimerBatch.serializer()) {
        appendPathSegments("api", "timerlist")
    }

    suspend fun remoteControlCall(key: RemoteControlKey) {
        networkDataSource.post {
            appendPathSegments("web", "remotecontrol")
            parameters.append("command", key.id.toString())
        }
    }

    suspend fun setPowerState(powerKey: RemoteControlPowerKey) {
        networkDataSource.post {
            appendPathSegments("api", "powerstate")
            parameters.append("newstate", powerKey.id.toString())
        }
    }

    private companion object {
        private const val ALL_SERVICES_TV =
            "1:7:1:0:0:0:0:0:0:0:(type%20==%201)%20||%20(type%20==%2017)%20||%20(type%20==%20195)%20||%20(type%20==%2025)%20ORDER%20BY%20name"
        private const val ALL_SERVICES_RADIO =
            "1:7:2:0:0:0:0:0:0:0:(type%20==%202)%20ORDER%20BY%20name"
        private const val ALL_PROVIDERS_TV =
            "1:7:1:0:0:0:0:0:0:0:(type%20==%201)%20||%20(type%20==%2017)%20||%20(type%20==%20195)%20||%20(type%20==%2025)%20FROM%20PROVIDERS%20ORDER%20BY%20name"
        private const val ALL_PROVIDERS_RADIO =
            "1:7:2:0:0:0:0:0:0:0:(type%20==%202)%20FROM%20PROVIDERS%20ORDER%20BY%20name"
    }
}