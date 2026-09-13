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

import io.github.deprec8.enigmadroid.data.constants.ServiceType
import io.github.deprec8.enigmadroid.data.model.Device
import io.github.deprec8.enigmadroid.data.model.api.ActionResultDto
import io.github.deprec8.enigmadroid.data.model.api.BouquetServiceDto
import io.github.deprec8.enigmadroid.data.model.api.MovieListDto
import io.github.deprec8.enigmadroid.data.model.api.NetworkDeviceInfo
import io.github.deprec8.enigmadroid.data.model.api.NetworkSignalInfo
import io.github.deprec8.enigmadroid.data.model.api.NetworkStatusInfo
import io.github.deprec8.enigmadroid.data.model.api.SatelliteListDto
import io.github.deprec8.enigmadroid.data.model.api.ServiceEpgDto
import io.github.deprec8.enigmadroid.data.model.api.ServiceListDto
import io.github.deprec8.enigmadroid.data.model.api.TimerDto
import io.github.deprec8.enigmadroid.data.model.api.TimerListDto
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal class NetworkDataSource {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
    }

    val client = HttpClient(OkHttp) {
        install(ContentEncoding) {
            gzip()
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    private inline fun <T, R> T.safeCall(block: T.() -> R): Result<R> {
        return try {
            Result.success(block())
        } catch (e: UnknownHostException) {
            Result.failure(e)
        } catch (e: ConnectException) {
            Result.failure(e)
        } catch (e: SocketTimeoutException) {
            Result.failure(e)
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(e)
        } catch (e: NoTransformationFoundException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    private suspend inline fun <reified T> get(
        device: Device, noinline urlBuilder: URLBuilder.() -> Unit
    ) = safeCall {
        client.get {
            url {
                url.takeFrom(device.getUrlBuilder())
                url.urlBuilder()
            }
        }.body<T>()
    }

    // Services

    suspend fun getBouquetsWithServices(device: Device, serviceType: ServiceType) =
        get<BouquetServiceDto>(device) {
            appendPathSegments("api", "getallservices")
            if (serviceType == ServiceType.Radio) {
                parameters.append(
                    "type", serviceType.string
                )
            }
        }

    suspend fun getProviders(device: Device, serviceType: ServiceType) =
        get<ServiceListDto>(device) {
            appendPathSegments("api", "getservices")
            parameters.append(
                "sRef", "${serviceType.reference} FROM PROVIDERS ORDER BY name"
            )
        }

    suspend fun getSatellites(device: Device, serviceType: ServiceType) =
        get<SatelliteListDto>(device) {
            appendPathSegments("api", "getsatellites")
            parameters.append(
                "stype", serviceType.string
            )
        }

    suspend fun getServices(device: Device, parentReference: String) = get<ServiceListDto>(device) {
        appendPathSegments("api", "getservices")
        parameters.append(
            "sRef", parentReference
        )
    }

    // Events

    suspend fun getServiceEpg(device: Device, serviceReference: String) =
        get<ServiceEpgDto>(device) {
            appendPathSegments("api", "epgservice")
            parameters.append("sRef", serviceReference)
            parameters.append("endTime", "10080")
        }

    suspend fun getMovieList(device: Device) = get<MovieListDto>(device) {
        appendPathSegments("api", "movielist")
        parameters.append("recursive", "true")
    }

    suspend fun getTimerBatch(device: Device) = get<TimerListDto>(device) {
        appendPathSegments("api", "timerlist")
    }

    // Device

    suspend fun getSignalInfo(device: Device) = get<NetworkSignalInfo>(device) {
        appendPathSegments("api", "tunersignal")
    }

    suspend fun getStatusInfo(device: Device) = get<NetworkStatusInfo>(device) {
        appendPathSegments("api", "statusinfo")
    }


    suspend fun getDeviceInfo(device: Device) = get<NetworkDeviceInfo>(device) {
        appendPathSegments("api", "deviceinfo")
    }

    suspend fun getScreenshot(device: Device) = safeCall {
        client.get {
            url {
                url.takeFrom(device.getUrlBuilder())
                appendPathSegments("grab")
                parameters.append("format", "png")
            }
        }.readRawBytes()
    }

    // Actions

    suspend fun addTimer(device: Device, timer: TimerDto) = get<ActionResultDto>(device) {
        appendPathSegments("api", "timeradd")
        parameters.append("sRef", timer.serviceReference)
        parameters.append("begin", timer.beginTimestamp.toString())
        parameters.append("end", timer.endTimestamp.toString())
        parameters.append("name", timer.title)
        parameters.append("disabled", timer.disabled.toString())
        parameters.append("justplay", timer.justPlay.toString())
        parameters.append("afterevent", timer.afterEventState.toString())
        parameters.append("repeated", timer.repeated.toString())
        parameters.append("description", timer.shortDescription)
    }

    suspend fun addTimerByEventId(
        device: Device, serviceReference: String, eventId: Int
    ) = get<ActionResultDto>(device) {
        appendPathSegments("api", "timeraddbyeventid")
        parameters.append("sRef", serviceReference)
        parameters.append("eventid", eventId.toString())
    }

    suspend fun editTimer(
        device: Device, oldTimer: TimerDto, newTimer: TimerDto
    ) = get<ActionResultDto>(device) {
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
        parameters.append("afterevent", newTimer.afterEventState.toString())
        parameters.append("dirname", oldTimer.directoryName)
        parameters.append("tags", oldTimer.tags)
        parameters.append("repeated", newTimer.repeated.toString())
        parameters.append("description", newTimer.shortDescription)
    }

    suspend fun toggleTimerStatus(device: Device, timer: TimerDto) = get<ActionResultDto>(device) {
        appendPathSegments("api", "timertogglestatus")
        parameters.append("sRef", timer.serviceReference)
        parameters.append("begin", timer.beginTimestamp.toString())
        parameters.append("end", timer.endTimestamp.toString())
    }

    suspend fun deleteTimer(device: Device, timer: TimerDto) = get<ActionResultDto>(device) {
        appendPathSegments("api", "timerdelete")
        parameters.append("sRef", timer.serviceReference)
        parameters.append("begin", timer.beginTimestamp.toString())
        parameters.append("end", timer.endTimestamp.toString())
    }

    suspend fun postRemoteControl(device: Device, key: String) = get<ActionResultDto>(device) {
        appendPathSegments("web", "remotecontrol")
        parameters.append("command", key)
    }

    suspend fun setPowerState(device: Device, state: String) = get<ActionResultDto>(device) {
        appendPathSegments("api", "powerstate")
        parameters.append("newstate", state)
    }

    suspend fun zap(device: Device, serviceReference: String) = get<ActionResultDto>(device) {
        appendPathSegments("api", "zap")
        parameters.append("sRef", serviceReference)
    }

    suspend fun renameMovie(
        device: Device, serviceReference: String, newName: String
    ) = get<ActionResultDto>(device) {
        appendPathSegments("api", "movierename")
        parameters.append("sRef", serviceReference)
        parameters.append("newname", newName)
    }

    suspend fun moveMovie(device: Device, serviceReference: String, dirName: String) =
        get<ActionResultDto>(device) {
            appendPathSegments("api", "moviemove")
            parameters.append("sRef", serviceReference)
            parameters.append("dirname", dirName)
        }

    suspend fun deleteMovie(device: Device, serviceReference: String) =
        get<ActionResultDto>(device) {
            appendPathSegments("api", "moviedelete")
            parameters.append("sRef", serviceReference)
        }
}