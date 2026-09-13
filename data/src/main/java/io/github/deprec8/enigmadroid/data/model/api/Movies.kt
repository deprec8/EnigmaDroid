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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Movie(
    val key: Long,
    val directory: String,
    val fileName: String,
    val serviceReference: String,
    val length: String,
    val lastSeen: Long,
    val fileSize: Long,
    val recordingTime: Long,
    val tags: String,
    val eventName: String,
    val serviceName: String,
    val shortDescription: String,
    val longDescription: String,
)

@Entity(
    tableName = "movies",
    primaryKeys = ["deviceId", "directory", "fileName"],
    indices = [Index(value = ["eventName"])]
)
data class MovieEntity(
    val deviceId: Long,
    val directory: String,
    val fileName: String,
    val serviceReference: String,
    val length: String,
    val lastSeen: Long,
    val fileSize: Long,
    val recordingTime: Long,
    val tags: String,
    val eventName: String,
    val shortDescription: String,
    val longDescription: String,
)

@Serializable
data class MovieListDto(
    @SerialName("directory") val directory: String,
    @SerialName("movies") val movies: List<MovieDto>,
    @SerialName("locations") val directories: List<String>
) {
    fun toMovieEntities(deviceId: Long) = movies.map { movieDto ->
        MovieEntity(
            deviceId = deviceId,
            directory = "TODO",
            fileName = movieDto.fileName,
            serviceReference = movieDto.serviceReference,
            length = movieDto.length,
            lastSeen = movieDto.lastSeen,
            fileSize = movieDto.fileSize,
            recordingTime = movieDto.recordingTime,
            tags = movieDto.tags,
            eventName = movieDto.eventName,
            shortDescription = movieDto.shortDescription,
            longDescription = movieDto.longDescription
        )
    }
}

@Serializable
data class MovieDto(
    @SerialName("filename") val fileName: String,
    @SerialName("serviceref") val serviceReference: String,
    @SerialName("length") val length: String,
    @SerialName("lastseen") val lastSeen: Long,
    @SerialName("filesize") val fileSize: Long,
    @SerialName("recordingtime") val recordingTime: Long,
    @SerialName("tags") val tags: String,
    @SerialName("eventname") val eventName: String = "N/A",
    @SerialName("servicename") val serviceName: String = "N/A",
    @SerialName("description") val shortDescription: String,
    @SerialName("descriptionExtended") val longDescription: String,
)