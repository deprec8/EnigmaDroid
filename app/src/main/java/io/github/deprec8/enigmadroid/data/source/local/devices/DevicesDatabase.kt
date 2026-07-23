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

package io.github.deprec8.enigmadroid.data.source.local.devices

import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Delete
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase
import androidx.room3.Update
import io.github.deprec8.enigmadroid.common.enums.RemoteControlKey
import kotlinx.coroutines.flow.Flow

@Entity
data class Device(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String = "",
    val ip: String = "",
    val isHttps: Boolean = false,
    val isLogin: Boolean = false,
    val user: String = "",
    val password: String = "",
    val port: String = "",
    val livePort: String = "",
) {

    fun buildUrl(endpoint: String) = buildString {
        append(if (isHttps) "https://" else "http://")
        if (isLogin) {
            append("${user}:${password}@")
        }
        append("${ip}:${port}/api/${endpoint.replace(" ", "%20")}")
    }

    fun buildUrl(button: RemoteControlKey) = buildString {
        append(if (isHttps) "https://" else "http://")
        if (isLogin) {
            append("${user}:${password}@")
        }
        append("${ip}:${port}/web/remotecontrol?command=${button.id}")
    }

    fun buildOwifUrl() = buildString {
        append(if (isHttps) "https://" else "http://")
        if (isLogin) {
            append("${user}:${password}@")
        }
        append("${ip}:${port}")
    }

    fun buildMovieStreamUrl(file: String) = buildString {
        append(if (isHttps) "https://" else "http://")
        if (isLogin) {
            append("${user}:${password}@")
        }
        append("${ip}:${port}/file?file=${file.replace(" ", "%20")}")
    }

    fun buildLiveStreamUrl(serviceReference: String) = buildString {
        append("http://")
        if (isLogin) {
            append("${user}:${password}@")
        }
        append("${ip}:${livePort}/${serviceReference.replace(" ", "%20")}")
    }

    fun buildScreenshotUrl() = buildString {
        append(if (isHttps) "https://" else "http://")
        if (isLogin) {
            append("${user}:${password}@")
        }
        append("${ip}:${port}/grab?format=png")
    }
}

@Database(entities = [Device::class], version = 1)
abstract class DevicesDatabase : RoomDatabase() {

    abstract fun devicesDao(): DevicesDao
}

@Dao
interface DevicesDao {

    @Insert
    suspend fun insert(device: Device): Long

    @Update
    suspend fun update(device: Device)

    @Delete
    suspend fun delete(device: Device)

    @Query("SELECT * FROM Device")
    fun getAll(): Flow<List<Device>>

    @Query("SELECT * FROM Device")
    suspend fun getAllStatic(): List<Device>

    @Query("SELECT * FROM Device WHERE id = :id")
    fun get(id: Int): Flow<Device?>

    @Query("SELECT * FROM Device WHERE id = :id")
    suspend fun getStatic(id: Int): Device?

    @Query("SELECT COUNT(*) FROM Device")
    suspend fun getCount(): Int
}