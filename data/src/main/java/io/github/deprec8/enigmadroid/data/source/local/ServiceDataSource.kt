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

package io.github.deprec8.enigmadroid.data.source.local

import io.github.deprec8.enigmadroid.core.common.ServiceType
import io.github.deprec8.enigmadroid.core.database.model.api.ServiceEntity
import io.github.deprec8.enigmadroid.data.model.api.BouquetEntity
import io.github.deprec8.enigmadroid.data.model.api.ProviderEntity
import io.github.deprec8.enigmadroid.data.source.local.database.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class ServiceDataSource(
    private val appDatabase: AppDatabase
) {
    suspend fun updateBouquets(bouquets: List<BouquetEntity>) {
        appDatabase.bouquetsDao().insertOrUpdateAll(bouquets)
    }

    suspend fun updateProviders(providers: List<ProviderEntity>) {
        appDatabase.providersDao().insertOrUpdateAll(providers)
    }

    suspend fun updateServices(services: List<ServiceEntity>) {
        appDatabase.servicesDao().insertServices(services)
    }

    suspend fun getBouquets(deviceId: Long, serviceType: ServiceType) =
        appDatabase.bouquetsDao().getAll(deviceId, serviceType)

    suspend fun getProviders(deviceId: Long, serviceType: ServiceType) =
        appDatabase.providersDao().getAll(deviceId, serviceType)

    suspend fun getServices(deviceId: Long, parentReference: String) =
        appDatabase.servicesDao().getServices(deviceId, parentReference)


}