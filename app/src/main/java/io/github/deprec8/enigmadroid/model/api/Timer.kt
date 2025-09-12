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
data class Timer(
    @SerialName("begin") val beginTimestamp: Long = 0L,
    @SerialName("description") val shortDescription: String = "",
    @SerialName("tags") val tags: String = "",
    @SerialName("always_zap") val alwaysZap: Int = 0,
    @SerialName("toggledisabled") val toggleDisabled: Int = 0,
    @SerialName("disabled") val disabled: Int = 0,
    @SerialName("repeated") val repeated: Int = 0,
    @SerialName("servicename") val serviceName: String = "",
    @SerialName("duration") val durationInSeconds: Int = 0,
    @SerialName("dirname") val directoryName: String = "",
    @SerialName("realend") val end: String = "",
    @SerialName("descriptionextended") val descriptionextended: String = "",
    @SerialName("name") val title: String = "",
    @SerialName("startprepare") val startprepare: Long = 0L,
    @SerialName("realbegin") val begin: String = "",
    @SerialName("end") val endTimestamp: Long = 0L,
    @SerialName("afterevent") val afterEvent: Int = 3,
    @SerialName("justplay") val justPlay: Int = 0,
    @SerialName("serviceref") val serviceReference: String = "",
    @SerialName("filename") val fileName: String? = null,
    @SerialName("state") val state: Int = 0,
    @SerialName("nextactivation") val nextActivation: String? = null,
    @SerialName("cancelled") val cancelled: Boolean = false,
    @SerialName("eit") val eit: Int = 0,
)