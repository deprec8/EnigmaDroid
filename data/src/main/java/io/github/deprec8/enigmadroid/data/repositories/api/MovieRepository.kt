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
import io.github.deprec8.enigmadroid.data.model.api.Movie
import io.github.deprec8.enigmadroid.data.source.local.dao.SyncDao
import io.github.deprec8.enigmadroid.data.source.local.dao.api.MoviesDao
import io.github.deprec8.enigmadroid.data.source.local.database.AppDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MovieRepository private constructor(
    private val appDatabase: AppDatabase,
    private val movieDao: MoviesDao,
    private val syncDao: SyncDao,
    private val networkDataSource: NetworkDataSource,
) {

    suspend fun observeMovies(deviceId: Long): Flow<List<Movie>> = movieDao.getAll(deviceId).map {
        it.map { entity -> entity.toMovie() }
    }

    fun observeSync(deviceId: Long): Flow<SyncMetadata?> =
        syncDao.get(deviceId, SyncCategory.MOVIES).map { entity ->
            entity?.toSyncMetadata()
        }

    suspend fun refresh(device: Device): RefreshResult {
        val timestamp = System.currentTimeMillis()

        return networkDataSource.getMovieList(device).fold(
            onSuccess = { dto ->
                appDatabase.withWriteTransaction {
                    movieDao.sync(
                        deviceId = device.id,
                        movies = dto.toMovieEntities(device.id),
                    )

                    syncDao.update(
                        deviceId = device.id,
                        category = SyncCategory.MOVIES,
                        timestamp = timestamp,
                        success = true,
                    )
                }

                RefreshResult.Success
            },
            onFailure = { exception ->
                syncDao.update(
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
        val syncMetadata = syncDao.get(
            deviceId = device.id,
            category = SyncCategory.MOVIES,
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