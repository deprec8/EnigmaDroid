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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class ActionResult(
    val success: Boolean, val message: String, val conflicts: List<Conflict>? = null
)

data class Conflict(
    val serviceReference: String,
    val serviceName: String,
    val name: String,
    val beginTimestamp: Long,
    val endTimestamp: Long
)

@Serializable
internal data class ActionResultDto(
    @SerialName("result") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("conflicts") val conflicts: List<ConflictDto>? = null
) {
    fun toActionResult() = ActionResult(
        success = success, message = message, conflicts = conflicts?.map { conflictDto ->
            Conflict(
                serviceReference = conflictDto.serviceReference,
                serviceName = conflictDto.serviceName,
                name = conflictDto.name,
                beginTimestamp = conflictDto.beginTimestamp.times(1000),
                endTimestamp = conflictDto.endTimestamp.times(1000)
            )
        })
}

@Serializable
internal data class ConflictDto(
    @SerialName("serviceref") val serviceReference: String,
    @SerialName("servicename") val serviceName: String,
    @SerialName("name") val name: String,
    @SerialName("begin") val beginTimestamp: Long,
    @SerialName("end") val endTimestamp: Long
)