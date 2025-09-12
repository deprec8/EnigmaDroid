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
data class DeviceInfo(
    @SerialName("tuners") val tuners: List<Tuner> = emptyList(),
    @SerialName("ifaces") val interfaces: List<Interface> = emptyList(),
    @SerialName("fp_version") val fpVersion: Int = 0,
    @SerialName("kernelver") val kernelVersion: String = "N/A",
    @SerialName("uptime") val uptime: String = "N/A",
    @SerialName("enigmaver") val enigmaVersion: String = "N/A",
    @SerialName("imagever") val imageVersion: String = "N/A",
    @SerialName("brand") val brand: String = "N/A",
    @SerialName("webifver") val webifVersion: String = "N/A",
    @SerialName("hdd") val hdds: List<HDD> = emptyList(),
    @SerialName("model") val model: String = "N/A",
    @SerialName("result") val result: Boolean = false
)