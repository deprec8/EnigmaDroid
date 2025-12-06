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

import LogEntrySerializer
import io.github.deprec8.enigmadroid.utils.HtmlDecodedStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Timer(
    @SerialName("begin") val beginTimestamp: Long = 0L,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("description") val shortDescription: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("tags") val tags: String = "",
    @SerialName("always_zap") val alwaysZap: Int = 0,
    @SerialName("toggledisabled") val toggleDisabled: Int = 0,
    @SerialName("disabled") val disabled: Int = 0,
    @SerialName("repeated") val repeated: Int = 0,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("servicename") val serviceName: String = "",
    @SerialName("duration") val durationInSeconds: Int = 0,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("dirname") val directoryName: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("realend") val end: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("descriptionextended") val descriptionextended: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("name") val title: String = "",
    @SerialName("startprepare") val startprepare: Long = 0L,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("realbegin") val begin: String = "",
    @SerialName("end") val endTimestamp: Long = 0L,
    @SerialName("afterevent") val afterEvent: Int = 3,
    @SerialName("justplay") val justPlay: Int = 0,
    @SerialName("serviceref") val serviceReference: String = "",
    @SerialName("filename") val fileName: String? = null,
    @SerialName("state") val state: Int = 0,
    @SerialName("nextactivation") val nextActivation: String? = null,
    @SerialName("cancelled") val cancelled: Boolean = false,
    @SerialName("eit") val eit: Int = 0,
    @Serializable(with = LogEntrySerializer::class) @SerialName("logentries") val logEntries: List<LogEntry> = emptyList()
)