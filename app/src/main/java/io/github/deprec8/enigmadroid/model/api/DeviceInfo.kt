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

package io.github.deprec8.enigmadroid.model.api

import androidx.compose.runtime.Immutable
import io.github.deprec8.enigmadroid.data.serialization.HtmlDecodedStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DeviceInfo(
    @SerialName("tuners") val tuners: List<Tuner> = emptyList(),
    @SerialName("ifaces") val interfaces: List<Interface> = emptyList(),
    @SerialName("hdd") val hdds: List<Hdd> = emptyList(),
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("brand") val brand: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("model") val model: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("chipset") val chipset: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("boxtype") val boxType: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("imagedistro") val imageDistro: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("imagever") val imageVersion: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("kernelver") val kernelVersion: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("enigmaver") val enigmaVersion: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("webifver") val owifVersion: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("oever") val oeSystemVersion: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("driverdate") val driverDate: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("uptime") val uptime: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("mem1") val totalMemory: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("mem2") val freeMemory: String = "N/A"
)

@Immutable
@Serializable
data class Hdd(
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("capacity") val capacity: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("mount") val mountDirectory: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("free") val freeSpace: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("model") val model: String = "N/A"
)

@Immutable
@Serializable
data class Interface(
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("ip") val ip: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("name") val name: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("friendlynic") val friendlyNic: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("gw") val gateway: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("linkspeed") val linkSpeed: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("firstpublic") val firstPublicIpv6: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("ipv4method") val ipv4Method: String = "N/A"
)

@Immutable
@Serializable
data class Tuner(
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("type") val type: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("name") val name: String = "N/A"
)