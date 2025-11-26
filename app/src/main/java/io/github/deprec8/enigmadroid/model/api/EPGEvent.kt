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

import io.github.deprec8.enigmadroid.utils.HtmlDecodedStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EPGEvent(
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("begin") val begin: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("sname") val serviceName: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("end") val end: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("title") val title: String = "N/A",
    @SerialName("genreid") val genreId: Int = 0,
    @SerialName("now_timestamp") val nowTimestamp: Long,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("shortdesc") val shortDescription: String = "N/A",
    @SerialName("begin_timestamp") val beginTimestamp: Long = 0L,
    @SerialName("duration_sec") val durationInSeconds: Int,
    @SerialName("sref") val serviceReference: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("longdesc") val longDescription: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("date") val date: String = "N/A",
    @SerialName("progress") val progress: Int = 0,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("genre") val genre: String = "N/A",
    @SerialName("id") val id: Int = 0
)