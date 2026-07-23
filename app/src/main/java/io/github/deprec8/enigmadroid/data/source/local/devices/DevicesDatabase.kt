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
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import io.github.deprec8.enigmadroid.common.enums.RemoteControlKey
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val host: String,
    val port: Int,
    val livePort: Int,
    val https: Boolean,
    val login: Boolean,
    val user: String,
    val password: String
) {

    fun buildUrl(endpoint: String) = buildString {
        append(if (https) "https://" else "http://")
        if (login) {
            append("${user}:${password}@")
        }
        append("${host}:${port}/api/${endpoint.replace(" ", "%20")}")
    }

    fun buildUrl(button: RemoteControlKey) = buildString {
        append(if (https) "https://" else "http://")
        if (login) {
            append("${user}:${password}@")
        }
        append("${host}:${port}/web/remotecontrol?command=${button.id}")
    }

    fun buildOwifUrl() = buildString {
        append(if (https) "https://" else "http://")
        if (login) {
            append("${user}:${password}@")
        }
        append("${host}:${port}")
    }

    fun buildMovieStreamUrl(file: String) = buildString {
        append(if (https) "https://" else "http://")
        if (login) {
            append("${user}:${password}@")
        }
        append("${host}:${port}/file?file=${file.replace(" ", "%20")}")
    }

    fun buildLiveStreamUrl(serviceReference: String) = buildString {
        append("http://")
        if (login) {
            append("${user}:${password}@")
        }
        append("${host}:${livePort}/${serviceReference.replace(" ", "%20")}")
    }

    fun buildScreenshotUrl() = buildString {
        append(if (https) "https://" else "http://")
        if (login) {
            append("${user}:${password}@")
        }
        append("${host}:${port}/grab?format=png")
    }
}

@Database(entities = [Device::class], version = 2)
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

val DEVICES_MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `devices` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `host` TEXT NOT NULL,
                `port` INTEGER NOT NULL,
                `livePort` INTEGER NOT NULL,
                `https` INTEGER NOT NULL,
                `login` INTEGER NOT NULL,
                `user` TEXT NOT NULL,
                `password` TEXT NOT NULL
            )
            """.trimIndent()
        )

        connection.execSQL(
            """
            INSERT INTO `devices` (`id`, `name`, `host`, `port`, `livePort`, `https`, `login`, `user`, `password`)
            SELECT `id`, `name`, `ip`, CAST(`port` AS INTEGER), CAST(`livePort` AS INTEGER), `isHttps`, `isLogin`, `user`, `password`
            FROM `Device`
            """.trimIndent()
        )

        connection.execSQL("DROP TABLE IF EXISTS `Device`")
    }
}