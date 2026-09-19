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
import io.github.deprec8.enigmadroid.data.model.api.DeviceInfo
import io.github.deprec8.enigmadroid.data.source.local.DevicesDataSource
import io.github.deprec8.enigmadroid.data.source.local.LastFetchCategory
import io.github.deprec8.enigmadroid.data.source.local.LastFetchLocalDataSource
import io.github.deprec8.enigmadroid.data.source.local.ServiceDataSource
import io.github.deprec8.enigmadroid.data.source.local.database.AppDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class ApiRepository private constructor(
    private val appDatabase: AppDatabase,
    private val networkDataSource: NetworkDataSource,
    private val devicesDataSource: DevicesDataSource,
    private val serviceDataSource: ServiceDataSource,
    private val lastFetchLocalDataSource: LastFetchLocalDataSource
) {
    val currentDevice = devicesDataSource.current

    private suspend fun getDeviceInfo(): Result<DeviceInfo> {
        val device = currentDevice.firstOrNull() ?: return Result.failure(MissingDeviceException())

        val refresh = lastFetchLocalDataSource.needsRefresh(LastFetchCategory.DEVICE_INFO).first()

        if (refresh) {
            networkDataSource.getDeviceInfo(device).onSuccess {
                appDatabase.infoDao().syncNetworkData(device.id, it)
                lastFetchLocalDataSource.updateLastFetchTimestamp(
                    LastFetchCategory.DEVICE_INFO, System.currentTimeMillis()
                )
            }.onFailure {
                return Result.failure(it)
            }
        }

        return runCatching { appDatabase.infoDao().getInfo(device.id) }
    }
}