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

package io.github.deprec8.enigmadroid.data.source.local.dao.api

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import io.github.deprec8.enigmadroid.data.model.api.BouquetEntity
import io.github.deprec8.enigmadroid.data.model.api.ServiceEntity

@Dao
internal interface ServicesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBouquets(bouquets: List<BouquetEntity>)

    @Query("DELETE FROM bouquets WHERE deviceId = :deviceId AND reference NOT IN (:references)")
    suspend fun clearMissingBouquets(deviceId: Long, references: List<String>)

    @Query("DELETE FROM bouquets WHERE deviceId = :deviceId")
    suspend fun clearBouquets(deviceId: Long)

    @Transaction
    suspend fun syncNetworkData(deviceId: Long, bouquets: List<BouquetEntity>) {
        if (bouquets.isEmpty()) {
            clearBouquets(deviceId)
            return
        }

        val currentReferences = bouquets.map { it.reference }

        clearMissingBouquets(deviceId, currentReferences)

        insertBouquets(bouquets)
    }

    @Query("SELECT * FROM services WHERE deviceId = :deviceId AND parentReference = :parentReference ORDER BY number ASC")
    suspend fun getServices(deviceId: Long, parentReference: String): List<ServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(bouquets: List<ServiceEntity>)

    @Query("DELETE FROM services WHERE deviceId = :deviceId AND parentReference = :parentReference AND reference NOT IN (:references)")
    suspend fun clearMissingServices(
        deviceId: Long, parentReference: String, references: List<String>
    )

    @Transaction
    suspend fun syncNetworkData(
        deviceId: Long, parentReference: String, services: List<ServiceEntity>
    ) {
        if (services.isEmpty()) {
            clearServices(deviceId, parentReference)
            return
        }

        val currentReferences = services.map { it.reference }

        clearMissingServices(deviceId, parentReference, currentReferences)

        insertServices(services)
    }

    @Query("DELETE FROM services WHERE deviceId = :deviceId AND parentReference = :parentReference")
    suspend fun clearServices(deviceId: Long, parentReference: String)

}