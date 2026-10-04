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
import io.github.deprec8.enigmadroid.data.model.api.DeviceInfo
import io.github.deprec8.enigmadroid.data.model.api.DeviceInfoDto
import io.github.deprec8.enigmadroid.data.model.api.DeviceInfoEntity
import io.github.deprec8.enigmadroid.data.model.api.HddEntity
import io.github.deprec8.enigmadroid.data.model.api.InterfaceEntity
import io.github.deprec8.enigmadroid.data.model.api.TunerEntity
import io.github.deprec8.enigmadroid.data.model.api.toDeviceInfo

@Dao
internal interface InfoDao {

    @Query("SELECT * FROM device_infos WHERE deviceId = :deviceId")
    suspend fun getDeviceInfo(deviceId: Long): DeviceInfoEntity

    @Query("SELECT * FROM hdds WHERE deviceId = :deviceId")
    suspend fun getHdds(deviceId: Long): List<HddEntity>

    @Query("SELECT * FROM interfaces WHERE deviceId = :deviceId")
    suspend fun getInterfaces(deviceId: Long): List<InterfaceEntity>

    @Query("SELECT * FROM tuners WHERE deviceId = :deviceId")
    suspend fun getTuners(deviceId: Long): List<TunerEntity>

    @Transaction
    suspend fun getInfo(deviceId: Long): DeviceInfo {
        val deviceInfo = getDeviceInfo(deviceId)
        val hdds = getHdds(deviceId)
        val interfaces = getInterfaces(deviceId)
        val tuners = getTuners(deviceId)

        return toDeviceInfo(deviceInfo, hdds, interfaces, tuners)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceInfo(deviceInfo: DeviceInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHdds(hdds: List<HddEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterfaces(interfaces: List<InterfaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTuners(tuners: List<TunerEntity>)

    @Transaction
    suspend fun syncNetworkData(deviceId: Long, deviceInfo: DeviceInfoDto) {
        insertDeviceInfo(deviceInfo.toDeviceInfoEntity(deviceId))
        insertHdds(deviceInfo.toHddEntities(deviceId))
        insertInterfaces(deviceInfo.toInterfaceEntities(deviceId))
        insertTuners(deviceInfo.toTunerEntities(deviceId))
    }

    @Query("DELETE FROM device_infos WHERE deviceId = :deviceId")
    suspend fun deleteDeviceInfo(deviceId: Long)

    @Query("DELETE FROM hdds WHERE deviceId = :deviceId")
    suspend fun deleteHdds(deviceId: Long)

    @Query("DELETE FROM interfaces WHERE deviceId = :deviceId")
    suspend fun deleteInterfaces(deviceId: Long)

    @Query("DELETE FROM tuners WHERE deviceId = :deviceId")
    suspend fun deleteTuners(deviceId: Long)

    @Transaction
    suspend fun clearInfo(deviceId: Long) {
        deleteDeviceInfo(deviceId)
        deleteHdds(deviceId)
        deleteInterfaces(deviceId)
        deleteTuners(deviceId)
    }

    @Query("DELETE FROM device_infos")
    suspend fun deleteAllDeviceInfo()

    @Query("DELETE FROM hdds")
    suspend fun deleteAllHdds()

    @Query("DELETE FROM interfaces")
    suspend fun deleteAllInterfaces()

    @Query("DELETE FROM tuners")
    suspend fun deleteAllTuners()

    @Transaction
    suspend fun clearAllInfo() {
        deleteAllDeviceInfo()
        deleteAllHdds()
        deleteAllInterfaces()
        deleteAllTuners()
    }
}