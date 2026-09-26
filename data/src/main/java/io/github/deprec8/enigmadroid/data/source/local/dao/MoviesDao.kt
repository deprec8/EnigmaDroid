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

package io.github.deprec8.enigmadroid.data.source.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import io.github.deprec8.enigmadroid.data.model.api.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface MoviesDao {

    @Query("SELECT * FROM movies WHERE deviceId = :deviceId ORDER BY beginTimestamp ASC")
    suspend fun getAll(deviceId: Long): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("DELETE FROM movies WHERE deviceId = :deviceId AND reference NOT IN (:references)")
    suspend fun deleteMissing(deviceId: Long, ids: List<Int>)

    @Transaction
    suspend fun sync(
        deviceId: Long, movies: List<MovieEntity>
    ) {
        deleteMissing(deviceId, listOf(1))

        insertAll(movies)
    }
}