/*
 * Copyright (C) 2025 deprec8
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

package io.github.deprec8.enigmadroid.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName("description") val shortDescription: String = "N/A",
    @SerialName("tags") val tags: String = "N/A",
    @SerialName("filename") val fileName: String = "",
    @SerialName("serviceref") val serviceReference: String = "",
    @SerialName("eventname") val eventName: String = "N/A",
    @SerialName("length") val length: String = "N/A",
    @SerialName("servicename") val serviceName: String = "N/A",
    @SerialName("begintime") val begin: String = "N/A",
    @SerialName("descriptionExtended") val longDescription: String = "N/A",
    @SerialName("filesize_readable") val filesizeReadable: String = "N/A"
)