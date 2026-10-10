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

package io.github.deprec8.enigmadroid.data.repositories.api

import androidx.room3.withWriteTransaction
import io.github.deprec8.enigmadroid.data.common.RefreshResult
import io.github.deprec8.enigmadroid.data.model.Device
import io.github.deprec8.enigmadroid.data.model.SyncCategory
import io.github.deprec8.enigmadroid.data.model.SyncMetadata
import io.github.deprec8.enigmadroid.data.model.api.DeviceInfo
import io.github.deprec8.enigmadroid.data.model.api.Hdd
import io.github.deprec8.enigmadroid.data.model.api.Interface
import io.github.deprec8.enigmadroid.data.model.api.Tuner
import io.github.deprec8.enigmadroid.data.source.local.database.AppDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DeviceInfoRepository internal constructor(
    private val appDatabase: AppDatabase,
    private val networkDataSource: NetworkDataSource,
) {

    fun observeDeviceInfo(deviceId: Long): Flow<DeviceInfo?> =
        appDatabase.infoDao().getDeviceInfo(deviceId).map {
            it?.toDeviceInfo()
        }

    fun observeHdds(deviceId: Long): Flow<List<Hdd>> =
        appDatabase.infoDao().getHdds(deviceId).map { hdds ->
            hdds.map { it.toHdd() }
        }

    fun observeTuners(deviceId: Long): Flow<List<Tuner>> =
        appDatabase.infoDao().getTuners(deviceId).map { tuners ->
            tuners.map { it.toTuner() }
        }

    fun observeInterfaces(deviceId: Long): Flow<List<Interface>> =
        appDatabase.infoDao().getInterfaces(deviceId).map { interfaces ->
            interfaces.map { it.toInterface() }
        }

    fun observeSync(deviceId: Long): Flow<SyncMetadata?> =
        appDatabase.syncDao().get(deviceId, SyncCategory.DEVICE_INFO).map { entity ->
            entity?.toSyncMetadata()
        }

    suspend fun refresh(device: Device): RefreshResult {
        val timestamp = System.currentTimeMillis()

        return networkDataSource.getDeviceInfo(device).fold(
            onSuccess = { dto ->
                appDatabase.withWriteTransaction {
                    appDatabase.infoDao().syncNetworkData(device.id, dto)
                    appDatabase.syncDao().update(
                        deviceId = device.id,
                        category = SyncCategory.DEVICE_INFO,
                        timestamp = timestamp,
                        success = true,
                    )
                }

                RefreshResult.Success
            },
            onFailure = { exception ->
                appDatabase.syncDao().update(
                    deviceId = device.id,
                    category = SyncCategory.MOVIES,
                    timestamp = timestamp,
                    success = false,
                )

                RefreshResult.Error(exception)
            },
        )
    }

    suspend fun refreshIfStale(
        device: Device,
        maxAge: Long = 5 * 60 * 1000L,
    ): RefreshResult? {
        val syncMetadata = appDatabase.syncDao().get(
            deviceId = device.id,
            category = SyncCategory.DEVICE_INFO,
        ).first()

        val lastRefresh = syncMetadata?.lastSuccess
        val stale = lastRefresh == null || System.currentTimeMillis() - lastRefresh > maxAge

        return if (stale) {
            refresh(device)
        } else {
            null
        }
    }
}