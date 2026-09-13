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

package io.github.deprec8.enigmadroid.data.source.local.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import io.github.deprec8.enigmadroid.data.model.Device
import io.github.deprec8.enigmadroid.data.model.SearchHistoryItem
import io.github.deprec8.enigmadroid.data.model.api.BouquetEntity
import io.github.deprec8.enigmadroid.data.model.api.DeviceInfoCore
import io.github.deprec8.enigmadroid.data.model.api.EventEntity
import io.github.deprec8.enigmadroid.data.model.api.Hdd
import io.github.deprec8.enigmadroid.data.model.api.Interface
import io.github.deprec8.enigmadroid.data.model.api.LogEntryEntity
import io.github.deprec8.enigmadroid.data.model.api.MovieEntity
import io.github.deprec8.enigmadroid.data.model.api.ProviderEntity
import io.github.deprec8.enigmadroid.data.model.api.TimerEntity
import io.github.deprec8.enigmadroid.data.model.api.Tuner
import io.github.deprec8.enigmadroid.data.source.local.dao.BouquetsDao
import io.github.deprec8.enigmadroid.data.source.local.dao.DevicesDao
import io.github.deprec8.enigmadroid.data.source.local.dao.ProvidersDao
import io.github.deprec8.enigmadroid.data.source.local.dao.SearchHistoriesDao
import io.github.deprec8.enigmadroid.data.source.local.dao.ServicesDao

@Database(
    entities = [Device::class, SearchHistoryItem::class, MovieEntity::class, TimerEntity::class, EventEntity::class, LogEntryEntity::class, BouquetEntity::class, ProviderEntity::class, DeviceInfoCore::class, Hdd::class, Interface::class, Tuner::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun devicesDao(): DevicesDao

    abstract fun searchHistoriesDao(): SearchHistoriesDao

    abstract fun bouquetsDao(): BouquetsDao

    abstract fun providersDao(): ProvidersDao

    abstract fun servicesDao(): ServicesDao
}