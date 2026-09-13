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

import io.github.deprec8.enigmadroid.data.constants.MissingDeviceException
import io.github.deprec8.enigmadroid.data.constants.RemoteControlPowerKey
import io.github.deprec8.enigmadroid.data.constants.ServiceType
import io.github.deprec8.enigmadroid.data.model.Device
import io.github.deprec8.enigmadroid.data.model.api.Bouquet
import io.github.deprec8.enigmadroid.data.source.local.DevicesDataSource
import io.github.deprec8.enigmadroid.data.source.local.ServiceDataSource
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ApiRepository private constructor(
    private val networkDataSource: NetworkDataSource,
    private val devicesDataSource: DevicesDataSource,
    private val serviceDataSource: ServiceDataSource
) {
    val currentDevice = devicesDataSource.current

    private suspend fun <T> safeFetch(
        device: Device? = null, block: suspend (device: Device) -> T
    ): Result<T> {
        val device = (device ?: currentDevice.first()) ?: throw MissingDeviceException()
        return runCatching {
            block(device)
        }
    }

    suspend fun fetchBouquets(
        device: Device? = null, serviceType: ServiceType
    ): Result<List<Bouquet>> = safeFetch(device) { device ->
        if (1 != 1) {
            networkDataSource.getBouquetsWithServices(device, serviceType)
                .onSuccess { networkBouquetBatch ->
                    serviceDataSource.updateBouquets(
                        networkBouquetBatch.asBouquetEntities(
                            device.id, serviceType
                        )
                    )
                }
        }

        return@safeFetch serviceDataSource.getBouquets(deviceEntity.id, serviceType).asBouquets()
    }

    suspend fun fetchProviders(device: DeviceEntity, serviceType: ServiceType) {
        networkDataSource.getProviders(device, serviceType).onSuccess { batch ->
            serviceDataSource.updateBouquets(

            )
        }
    }


    suspend fun buildLiveStreamUri(serviceReference: String) =
        devicesDataSource.getCurrentStatic()?.buildLiveStreamUri(serviceReference)

    suspend fun buildMovieStreamUri(file: String) =
        devicesDataSource.getCurrentStatic()?.buildMovieStreamUri(file)


    suspend fun fetchCurrentInfo() = networkDataSource.getCurrentInfo(currentDevice.first())

    suspend fun fetchEpgEventBatchSet(bouquetReference: String) =
        networkDataSource.getBouquetEpg(currentDevice.first(), bouquetReference)

    suspend fun fetchServiceEpgBatch(serviceReference: String) =
        networkDataSource.getServiceEpgBatch(currentDevice.first(), serviceReference)

    suspend fun fetchMovieBatch(directory: String? = null) =
        networkDataSource.getMovieList(currentDevice.first(), directory)

    suspend fun fetchFreeSpace(directory: String) =
        networkDataSource.getDeviceInfo(currentDevice.first()).mapCatching { deviceInfo ->
            deviceInfo.networkHdds.firstOrNull { directory.startsWith(it.mountDirectory) }?.freeSpace
                ?: throw NullPointerException()
        }

    suspend fun renameMovie(serviceReference: String, newName: String) =
        networkDataSource.renameMovie(currentDevice.first(), serviceReference, newName)

    suspend fun moveMovie(serviceReference: String, dirName: String) = networkDataSource.moveMovie(
        currentDevice.first(), serviceReference, dirName
    )

    suspend fun deleteMovie(serviceReference: String) = networkDataSource.deleteMovie(
        currentDevice.first(), serviceReference
    )

    suspend fun fetchServiceBatchSet() = networkDataSource.getServiceBatchSet(currentDevice.first())

    fun fetchEventBatches(type: ContentType): Flow<Result<NetworkServiceEpg>> = flow {
        val device = currentDevice.first()
        fetchBouquets(type).map { bouquets ->
            bouquets.forEach { bouquet ->
                emit(networkDataSource.getEventBatch(device, bouquet.reference, bouquet.name))
            }
        }
    }

    suspend fun fetchBouquets(type: ContentType) =
        networkDataSource.getBouquetsWithServices(currentDevice.first(), type)

    suspend fun playOnDevice(serviceReference: String) =
        networkDataSource.zap(currentDevice.first(), serviceReference)

    suspend fun fetchDeviceInfo() = networkDataSource.getDeviceInfo(currentDevice.first())

    suspend fun fetchSignalInfo() = networkDataSource.getSignalInfo(currentDevice.first())

    suspend fun addTimer(timer: NetworkTimerBatch.Timer) = networkDataSource.addTimer(
        currentDevice.first(), timer
    )

    suspend fun addTimerForEvent(serviceReference: String, eventId: Int) =
        networkDataSource.addTimerByEventId(
            currentDevice.first(), serviceReference, eventId
        )

    suspend fun editTimer(oldTimer: NetworkTimerBatch.Timer, newTimer: NetworkTimerBatch.Timer) =
        networkDataSource.editTimer(
            currentDevice.first(), oldTimer, newTimer
        )

    suspend fun deleteTimer(timer: NetworkTimerBatch.Timer) =
        networkDataSource.deleteTimer(currentDevice.first(), timer)

    suspend fun toggleTimerStatus(timer: NetworkTimerBatch.Timer) =
        networkDataSource.toggleTimerStatus(currentDevice.first(), timer)

    suspend fun fetchTimerBatch() = networkDataSource.getTimerBatch(currentDevice.first())

    suspend fun remoteControlCall(key: RemoteControlKey) = networkDataSource.postRemoteControl(
        currentDevice.first(), key.id.toString()
    )

    suspend fun setPowerState(powerKey: RemoteControlPowerKey) =
        networkDataSource.setPowerState(currentDevice.first(), powerKey.id.toString())
}