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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(
    tableName = "events", primaryKeys = ["deviceId", "serviceReference", "id", "beginTimestamp"]
)
internal data class EventEntity(
    val deviceId: Long,
    val serviceReference: String,
    val id: Int,
    val beginTimestamp: Long,
    val endTimestamp: Long,
    val title: String,
    val shortDescription: String,
    val longDescription: String,
    val genre: String
)

@Serializable
internal data class ServiceEpgDto(
    @SerialName("events") val events: List<EventDto>,
) {
    fun toEventEntities(deviceId: Long, serviceReference: String) = events.map { eventDto ->
        EventEntity(
            deviceId = deviceId,
            id = eventDto.id,
            beginTimestamp = eventDto.beginTimestamp.times(1000),
            endTimestamp = eventDto.beginTimestamp.plus(eventDto.durationInSeconds).times(1000),
            serviceReference = serviceReference,
            title = eventDto.title,
            shortDescription = eventDto.shortDescription,
            longDescription = eventDto.longDescription,
            genre = eventDto.genre
        )
    }
}

@Serializable
internal data class EventDto(
    @SerialName("id") val id: Int,
    @SerialName("begin_timestamp") val beginTimestamp: Long,
    @SerialName("duration_sec") val durationInSeconds: Long,
    @SerialName("sref") val serviceReference: String,
    @SerialName("now_timestamp") val nowTimestamp: Long,
    @SerialName("title") val title: String,
    @SerialName("shortdesc") val shortDescription: String,
    @SerialName("longdesc") val longDescription: String,
    @SerialName("sname") val serviceName: String,
    @SerialName("genre") val genre: String
)