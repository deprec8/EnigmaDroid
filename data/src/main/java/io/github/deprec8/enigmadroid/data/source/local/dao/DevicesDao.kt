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
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import io.github.deprec8.enigmadroid.data.model.Device
import kotlinx.coroutines.flow.Flow

@Dao
interface DevicesDao {

    @Insert
    suspend fun insert(deviceEntity: Device): Long

    @Update
    suspend fun update(deviceEntity: Device)

    @Delete
    suspend fun delete(deviceEntity: Device)

    @Query("SELECT * FROM devices")
    fun getAll(): Flow<List<Device>>

    @Query(
        """ 
        SELECT COALESCE(
        (
            SELECT id
            FROM devices
            WHERE id < :id
            ORDER BY id DESC
            LIMIT 1
        ),
        (
            SELECT id
            FROM devices
            WHERE id > :id
            ORDER BY id ASC
            LIMIT 1
        )
    )
    """
    )
    suspend fun getPreviousOrNextId(id: Long): Long?

    @Query("SELECT * FROM devices WHERE id = :id")
    fun get(id: Long): Flow<Device?>

    @Query("SELECT * FROM devices WHERE id = :id")
    suspend fun getStatic(id: Long): Device?

    @Query("SELECT COUNT(*) FROM devices")
    suspend fun getCount(): Int
}