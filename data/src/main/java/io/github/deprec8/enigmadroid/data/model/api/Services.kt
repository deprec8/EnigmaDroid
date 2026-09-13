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
import androidx.room3.Index
import io.github.deprec8.enigmadroid.data.constants.ServiceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Bouquet(
    val key: String, val reference: String, val name: String
)

data class Provider(
    val key: String, val reference: String, val name: String
)

data class Satellite(
    val key: String, val reference: String, val name: String
)

data class Service(
    val key: String, val number: Int, val reference: String, val name: String
)

@Entity(
    tableName = "bouquets",
    primaryKeys = ["deviceId", "reference"],
    indices = [Index(value = ["type", "position"])]
)
data class BouquetEntity(
    val deviceId: Long,
    val position: Int,
    val type: ServiceType,
    val reference: String,
    val name: String
) {
    fun toBouquet() = Bouquet(
        key = "$deviceId:$reference", reference = reference, name = name
    )
}

@Entity(
    tableName = "providers",
    primaryKeys = ["deviceId", "reference"],
    indices = [Index(value = ["type", "position"])]
)
data class ProviderEntity(
    val deviceId: Long,
    val position: Int,
    val type: ServiceType,
    val reference: String,
    val name: String
) {
    fun toProvider() = Provider(
        key = "$deviceId:$reference", reference = reference, name = name
    )
}

@Entity(
    tableName = "satellites",
    primaryKeys = ["deviceId", "reference"],
    indices = [Index(value = ["type", "position"])]
)
data class SatelliteEntity(
    val deviceId: Long,
    val position: Int,
    val type: ServiceType,
    val reference: String,
    val name: String
) {
    fun toSatellite() = Satellite(
        key = "$deviceId:$reference", reference = reference, name = name
    )
}

@Entity(
    tableName = "services",
    primaryKeys = ["deviceId", "parentReference", "reference", "uniquePosition"],
    indices = [Index(value = ["position"])]
)
data class ServiceEntity(
    val deviceId: Long,
    val parentReference: String,
    val uniquePosition: Int,
    val position: Int,
    val reference: String,
    val name: String
) {
    fun toService() = Service(
        key = "$deviceId:$parentReference:$reference",
        number = position,
        reference = reference,
        name = name
    )
}

@Serializable
data class BouquetServiceDto(
    @SerialName("services") val bouquets: List<BouquetDto>
) {
    fun toBouquetEntities(deviceId: Long) = bouquets.map { bouquet ->
    }
}

@Serializable
data class BouquetDto(
    @SerialName("servicereference") val reference: String,
    @SerialName("servicename") val name: String,
    @SerialName("subservices") val services: List<ServiceDto>
)

@Serializable
data class ServiceListDto(
    @SerialName("services") val services: List<ServiceDto>
)

@Serializable
data class ServiceDto(
    @SerialName("servicereference") val reference: String,
    @SerialName("servicename") val name: String,
    @SerialName("pos") val position: Int,
)

@Serializable
data class SatelliteListDto(
    @SerialName("satellites") val satellites: List<SatelliteDto>
) {
    fun toSatelliteEntities(deviceId: Long, serviceType: ServiceType) =
        satellites.mapIndexed { index, satelliteDto ->
            SatelliteEntity(
                deviceId = deviceId,
                position = index,
                type = serviceType,
                reference = satelliteDto.reference,
                name = satelliteDto.name
            )
        }
}

@Serializable
data class SatelliteDto(
    @SerialName("service") val reference: String, @SerialName("name") val name: String
)