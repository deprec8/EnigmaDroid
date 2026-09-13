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
import io.github.deprec8.enigmadroid.core.common.ServiceType
import io.github.deprec8.enigmadroid.core.database.model.api.BouquetEntity
import io.github.deprec8.enigmadroid.core.database.model.api.ProviderEntity
import io.github.deprec8.enigmadroid.core.database.model.api.ServiceEntity
import kotlin.collections.map

@Dao
interface BouquetsDao {

    @Query("SELECT * FROM bouquets WHERE deviceId = :deviceId AND type = :serviceType ORDER BY position ASC")
    suspend fun getAll(deviceId: Long, serviceType: ServiceType): List<BouquetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(bouquets: List<BouquetEntity>)

    @Query("DELETE FROM bouquets WHERE deviceId = :deviceId AND reference NOT IN (:references)")
    suspend fun deleteMissingEntries(deviceId: Long, references: List<String>)

    @Transaction
    suspend fun syncNetworkData(deviceId: Long, bouquets: List<BouquetEntity>) {
        if (bouquets.isEmpty()) {
            deleteAllForDevice(deviceId)
            return
        }

        val currentReferences = bouquets.map { it.reference }

        deleteMissingEntries(deviceId, currentReferences)

        insertOrUpdateAll(bouquets)
    }

    @Query("DELETE FROM bouquets WHERE deviceId = :deviceId")
    suspend fun deleteAllForDevice(deviceId: Long)
}

@Dao
interface ProvidersDao {

    @Query("SELECT * FROM providers WHERE deviceId = :deviceId ORDER BY position ASC")
    suspend fun getAll(deviceId: Long, serviceType: ServiceType): List<ProviderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(bouquets: List<ProviderEntity>)

    @Query("DELETE FROM providers WHERE deviceId = :deviceId AND reference NOT IN (:references)")
    suspend fun deleteMissingEntries(deviceId: Long, references: List<String>)

    @Transaction
    suspend fun syncNetworkData(deviceId: Long, providers: List<ProviderEntity>) {
        if (providers.isEmpty()) {
            deleteAllForDevice(deviceId)
            return
        }

        val currentReferences = providers.map { it.reference }

        deleteMissingEntries(deviceId, currentReferences)

        insertOrUpdateAll(providers)
    }

    @Query("DELETE FROM providers WHERE deviceId = :deviceId")
    suspend fun deleteAllForDevice(deviceId: Long)
}

@Dao
interface ServicesDao {

    @Query("SELECT * FROM services WHERE deviceId = :deviceId AND parentReference = :parentReference ORDER BY number ASC")
    suspend fun getAll(deviceId: Long, parentReference: String): List<ProviderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bouquets: List<ServiceEntity>)

    @Query("DELETE FROM services WHERE deviceId = :deviceId AND parentReference = :parentReference AND reference NOT IN (:references)")
    suspend fun deleteMissing(deviceId: Long, parentReference: String, references: List<String>)

    @Transaction
    suspend fun syncNetworkData(
        deviceId: Long, parentReference: String, services: List<ServiceEntity>
    ) {
        if (services.isEmpty()) {
            deleteAll(deviceId, parentReference)
            return
        }

        val currentReferences = services.map { it.reference }

        deleteMissing(deviceId, parentReference, currentReferences)

        insertAll(services)
    }

    @Query("DELETE FROM services WHERE deviceId = :deviceId AND parentReference = :parentReference")
    suspend fun deleteAll(deviceId: Long, parentReference: String)
}