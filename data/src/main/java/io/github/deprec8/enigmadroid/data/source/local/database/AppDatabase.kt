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
import io.github.deprec8.enigmadroid.data.model.SearchHistoryItemEntity
import io.github.deprec8.enigmadroid.data.model.SyncMetadataEntity
import io.github.deprec8.enigmadroid.data.model.api.BouquetEntity
import io.github.deprec8.enigmadroid.data.model.api.BouquetServiceEntity
import io.github.deprec8.enigmadroid.data.model.api.DeviceInfoEntity
import io.github.deprec8.enigmadroid.data.model.api.EventEntity
import io.github.deprec8.enigmadroid.data.model.api.HddEntity
import io.github.deprec8.enigmadroid.data.model.api.InterfaceEntity
import io.github.deprec8.enigmadroid.data.model.api.LogEntryEntity
import io.github.deprec8.enigmadroid.data.model.api.MovieEntity
import io.github.deprec8.enigmadroid.data.model.api.SatelliteServiceEntity
import io.github.deprec8.enigmadroid.data.model.api.ServiceEntity
import io.github.deprec8.enigmadroid.data.model.api.TimerEntity
import io.github.deprec8.enigmadroid.data.model.api.TunerEntity
import io.github.deprec8.enigmadroid.data.source.local.dao.DevicesDao
import io.github.deprec8.enigmadroid.data.source.local.dao.SearchHistoriesDao
import io.github.deprec8.enigmadroid.data.source.local.dao.SyncDao
import io.github.deprec8.enigmadroid.data.source.local.dao.api.EpgDao
import io.github.deprec8.enigmadroid.data.source.local.dao.api.InfoDao
import io.github.deprec8.enigmadroid.data.source.local.dao.api.MoviesDao
import io.github.deprec8.enigmadroid.data.source.local.dao.api.ServicesDao
import io.github.deprec8.enigmadroid.data.source.local.dao.api.TimersDao

@Database(
    entities = [Device::class, SearchHistoryItemEntity::class, MovieEntity::class, TimerEntity::class, EventEntity::class, LogEntryEntity::class, BouquetEntity::class, DeviceInfoEntity::class, HddEntity::class, InterfaceEntity::class, TunerEntity::class, ServiceEntity::class, BouquetServiceEntity::class, SatelliteServiceEntity::class, SyncMetadataEntity::class],
    version = 1
)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun devicesDao(): DevicesDao

    abstract fun searchHistoriesDao(): SearchHistoriesDao

    abstract fun syncDao(): SyncDao

    abstract fun infoDao(): InfoDao

    abstract fun servicesDao(): ServicesDao

    abstract fun epgDao(): EpgDao

    abstract fun moviesDao(): MoviesDao

    abstract fun timersDao(): TimersDao
}