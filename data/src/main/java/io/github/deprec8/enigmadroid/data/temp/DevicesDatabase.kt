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

package io.github.deprec8.enigmadroid.data.temp

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

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
            FROM `DeviceEntity`
            """.trimIndent()
        )

        connection.execSQL("DROP TABLE IF EXISTS `DeviceEntity`")
    }
}