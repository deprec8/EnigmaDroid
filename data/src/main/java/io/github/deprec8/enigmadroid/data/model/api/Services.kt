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

package io.github.deprec8.enigmadroid.data.model.api

import androidx.room3.Entity
import io.github.deprec8.enigmadroid.data.constants.ServiceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Bouquets

@Entity(
    tableName = "bouquets", primaryKeys = ["deviceId", "reference"]
)
internal data class BouquetEntity(
    val deviceId: Long, val reference: String, val name: String, val type: ServiceType
)

@Entity(
    tableName = "bouquets_services",
    primaryKeys = ["bouquetReference", "serviceReference", "position"]
)
internal data class BouquetServiceEntity(
    val bouquetReference: String,
    val serviceReference: String,
    val displayPosition: Int,
    val position: Int,
    val markerName: String? = null
)

@Serializable
internal data class BouquetListDto(
    @SerialName("services") val bouquets: List<BouquetDto>
) {
    fun toBouquetEntities(deviceId: Long, serviceType: ServiceType) = bouquets.map { bouquet ->
        BouquetEntity(
            deviceId = deviceId,
            reference = bouquet.reference,
            name = bouquet.name,
            type = serviceType
        )
    }

    fun toBouquetServiceEntities(deviceId: Long) = bouquets.flatMap { bouquet ->
        bouquet.services.mapIndexedNotNull { index, serviceDto ->
            val flag = serviceDto.reference.split(":")[1].toInt()

            if ((flag and 320) == 320) {
                null
            } else {
                BouquetServiceEntity(
                    bouquetReference = bouquet.reference,
                    serviceReference = serviceDto.reference,
                    displayPosition = serviceDto.position,
                    position = index,
                    markerName = if ((flag and 64) != 0) serviceDto.name else null,
                )
            }
        }
    }
}

@Serializable
internal data class BouquetDto(
    @SerialName("servicereference") val reference: String,
    @SerialName("servicename") val name: String,
    @SerialName("subservices") val services: List<ServiceDto>
)

// Satellites

@Entity(
    tableName = "satellites", primaryKeys = ["deviceId", "reference"]
)
internal data class SatelliteEntity(
    val deviceId: Long,
    val reference: String,
    val name: String,
    val type: ServiceType,
)

@Entity(
    tableName = "satellites_services", primaryKeys = ["satelliteReference", "serviceReference"]
)
internal data class SatelliteServiceEntity(
    val satelliteReference: String, val serviceReference: String
)

@Serializable
internal data class SatelliteListDto(
    @SerialName("satellites") val satellites: List<SatelliteDto>
) {
    fun toSatelliteEntities(deviceId: Long, serviceType: ServiceType) =
        satellites.map { satelliteDto ->
            SatelliteEntity(
                deviceId = deviceId,
                type = serviceType,
                reference = satelliteDto.reference,
                name = satelliteDto.name
            )
        }
}

@Serializable
internal data class SatelliteDto(
    @SerialName("service") val reference: String, @SerialName("name") val name: String
)

internal fun ServiceListDto.toSatelliteServiceEntities(deviceId: Long, satelliteReference: String) =
    this.services.map { serviceDto ->
        SatelliteServiceEntity(
            satelliteReference = satelliteReference, serviceReference = serviceDto.reference
        )
    }

// Services

@Entity(
    tableName = "services", primaryKeys = ["deviceId", "reference"]
)
internal data class ServiceEntity(
    val deviceId: Long,
    val reference: String,
    val name: String,
    val provider: String? = null,
    val type: ServiceType? = null
)

@Serializable
internal data class ServiceListDto(
    @SerialName("services") val services: List<ServiceDto>
) {
    fun toServiceEntities(deviceId: Long) = services.map { serviceDto ->
        ServiceEntity(
            deviceId = deviceId,
            reference = serviceDto.reference,
            name = serviceDto.name,
            provider = serviceDto.provider.ifBlank { null })
    }
}

@Serializable
internal data class ServiceDto(
    @SerialName("servicereference") val reference: String,
    @SerialName("servicename") val name: String,
    @SerialName("pos") val position: Int,
    @SerialName("provider") val provider: String,
)