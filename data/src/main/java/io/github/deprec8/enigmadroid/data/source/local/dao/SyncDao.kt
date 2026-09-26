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
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import io.github.deprec8.enigmadroid.data.model.SyncCategory
import io.github.deprec8.enigmadroid.data.model.SyncMetadataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
internal interface SyncDao {

    @Query("SELECT * FROM sync_metadata WHERE deviceId = :deviceId AND category = :category")
    fun get(deviceId: Long, category: SyncCategory): Flow<SyncMetadataEntity?>

    @Upsert
    suspend fun upsert(entity: SyncMetadataEntity)

    @Transaction
    suspend fun update(
        deviceId: Long,
        category: SyncCategory,
        timestamp: Long,
        success: Boolean,
    ) {
        val current = get(deviceId, category).first()

        upsert(
            SyncMetadataEntity(
                deviceId = deviceId,
                category = category,
                lastAttempt = timestamp,
                lastSuccess = if (success) timestamp
                else current?.lastSuccess,
            )
        )
    }
}