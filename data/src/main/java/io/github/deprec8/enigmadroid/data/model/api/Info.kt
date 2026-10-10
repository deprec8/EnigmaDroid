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
import androidx.room3.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Device

data class DeviceInfo(
    val brand: String,
    val model: String,
    val chipset: String,
    val boxType: String,
    val imageDistro: String,
    val imageVersion: String,
    val kernelVersion: String,
    val enigmaVersion: String,
    val owifVersion: String,
    val oeSystemVersion: String,
    val driverDate: String,
    val uptime: String,
    val totalMemory: String,
    val freeMemory: String,
)

data class Hdd(
    val mountDirectory: String, val capacity: String, val freeSpace: String, val model: String
)

data class Interface(
    val ip: String,
    val name: String,
    val friendlyNic: String,
    val gateway: String,
    val linkSpeed: String,
    val firstPublicIpv6: String,
    val ipv4Method: String
)

data class Tuner(
    val number: Int, val type: String, val name: String
)

@Entity(tableName = "device_infos")
data class DeviceInfoEntity(
    @PrimaryKey val deviceId: Long,
    val brand: String,
    val model: String,
    val chipset: String,
    val boxType: String,
    val imageDistro: String,
    val imageVersion: String,
    val kernelVersion: String,
    val enigmaVersion: String,
    val owifVersion: String,
    val oeSystemVersion: String,
    val driverDate: String,
    val uptime: String,
    val totalMemory: String,
    val freeMemory: String,
) {
    fun toDeviceInfo() = DeviceInfo(
        brand = brand,
        model = model,
        chipset = chipset,
        boxType = boxType,
        imageDistro = imageDistro,
        imageVersion = imageVersion,
        kernelVersion = kernelVersion,
        enigmaVersion = enigmaVersion,
        owifVersion = owifVersion,
        oeSystemVersion = oeSystemVersion,
        driverDate = driverDate,
        uptime = uptime,
        totalMemory = totalMemory,
        freeMemory = freeMemory
    )
}

@Entity(tableName = "hdds", primaryKeys = ["deviceId", "mountDirectory"])
data class HddEntity(
    val deviceId: Long,
    val mountDirectory: String,
    val capacity: String,
    val freeSpace: String,
    val model: String
) {
    fun toHdd() = Hdd(
        mountDirectory = mountDirectory, capacity = capacity, freeSpace = freeSpace, model = model
    )
}

@Entity(tableName = "interfaces", primaryKeys = ["deviceId", "ip"])
data class InterfaceEntity(
    val deviceId: Long,
    val ip: String,
    val name: String,
    val friendlyNic: String,
    val gateway: String,
    val linkSpeed: String,
    val firstPublicIpv6: String,
    val ipv4Method: String
) {
    fun toInterface() = Interface(
        ip = ip,
        name = name,
        friendlyNic = friendlyNic,
        gateway = gateway,
        linkSpeed = linkSpeed,
        firstPublicIpv6 = firstPublicIpv6,
        ipv4Method = ipv4Method
    )
}

@Entity(tableName = "tuners", primaryKeys = ["deviceId", "number"])
data class TunerEntity(
    val deviceId: Long, val number: Int, val type: String, val name: String
) {
    fun toTuner() = Tuner(
        number = number, type = type, name = name
    )
}

@Serializable
internal data class DeviceInfoDto(
    @SerialName("tuners") val tuners: List<TunerDto>,
    @SerialName("ifaces") val interfaces: List<InterfaceDto> = emptyList(),
    @SerialName("hdd") val hdds: List<HddDto> = emptyList(),
    @SerialName("brand") val brand: String = "N/A",
    @SerialName("model") val model: String = "N/A",
    @SerialName("chipset") val chipset: String = "N/A",
    @SerialName("boxtype") val boxType: String = "N/A",
    @SerialName("imagedistro") val imageDistro: String = "N/A",
    @SerialName("imagever") val imageVersion: String = "N/A",
    @SerialName("kernelver") val kernelVersion: String = "N/A",
    @SerialName("enigmaver") val enigmaVersion: String = "N/A",
    @SerialName("webifver") val owifVersion: String = "N/A",
    @SerialName("oever") val oeSystemVersion: String = "N/A",
    @SerialName("driverdate") val driverDate: String = "N/A",
    @SerialName("uptime") val uptime: String,
    @SerialName("mem1") val totalMemory: String,
    @SerialName("mem2") val freeMemory: String
) {
    fun toDeviceInfoEntity(deviceId: Long) = DeviceInfoEntity(
        deviceId = deviceId,
        brand = brand,
        model = model,
        chipset = chipset,
        boxType = boxType,
        imageDistro = imageDistro,
        imageVersion = imageVersion,
        kernelVersion = kernelVersion,
        enigmaVersion = enigmaVersion,
        owifVersion = owifVersion,
        oeSystemVersion = oeSystemVersion,
        driverDate = driverDate,
        uptime = uptime,
        totalMemory = totalMemory,
        freeMemory = freeMemory
    )

    fun toHddEntities(deviceId: Long) = hdds.map { hdd ->
        HddEntity(
            deviceId = deviceId,
            mountDirectory = hdd.mountDirectory,
            capacity = hdd.capacity,
            freeSpace = hdd.freeSpace,
            model = hdd.model
        )
    }

    fun toInterfaceEntities(deviceId: Long) = interfaces.map { iface ->
        InterfaceEntity(
            deviceId = deviceId,
            ip = iface.ip,
            name = iface.name,
            friendlyNic = iface.friendlyNic,
            gateway = iface.gateway,
            linkSpeed = iface.linkSpeed,
            firstPublicIpv6 = iface.firstPublicIpv6,
            ipv4Method = iface.ipv4Method
        )
    }

    fun toTunerEntities(deviceId: Long) = tuners.mapIndexed { index, tuner ->
        TunerEntity(
            deviceId = deviceId, number = index + 1, type = tuner.type, name = tuner.name
        )
    }
}

@Serializable
internal data class HddDto(
    @SerialName("capacity") val capacity: String = "N/A",
    @SerialName("mount") val mountDirectory: String = "N/A",
    @SerialName("free") val freeSpace: String = "N/A",
    @SerialName("model") val model: String = "N/A"
)

@Serializable
internal data class InterfaceDto(
    @SerialName("ip") val ip: String = "N/A",
    @SerialName("name") val name: String = "N/A",
    @SerialName("friendlynic") val friendlyNic: String = "N/A",
    @SerialName("gw") val gateway: String = "N/A",
    @SerialName("linkspeed") val linkSpeed: String = "N/A",
    @SerialName("firstpublic") val firstPublicIpv6: String = "N/A",
    @SerialName("ipv4method") val ipv4Method: String = "N/A"
)

@Serializable
internal data class TunerDto(
    @SerialName("type") val type: String = "N/A", @SerialName("name") val name: String = "N/A"
)

// Status

data class SignalInfo(
    val agc: Double,
    val tunerNumber: Int,
    val snr: Double,
    val tunerType: String,
)

data class StatusInfo(
    val standby: Boolean, val muted: Boolean, val volume: Int, val currentServiceReference: String
)

@Serializable
internal data class SignalInfoDto(
    @SerialName("agc") val agc: String,
    @SerialName("tunernumber") val tunerNumber: String,
    @SerialName("snr") val snr: String,
    @SerialName("tunertype") val tunerType: String,
) {
    fun toSignalInfo(): SignalInfo = SignalInfo(
        agc = agc.toDouble(),
        tunerNumber = tunerNumber.toInt(),
        snr = snr.toDouble(),
        tunerType = tunerType
    )
}

@Serializable
internal data class StatusInfoDto(
    @SerialName("inStandby") val standby: String,
    @SerialName("muted") val muted: Boolean,
    @SerialName("volume") val volume: Int,
    @SerialName("currservice_serviceref") val currentServiceReference: String
) {
    fun toStatusInfo(): StatusInfo = StatusInfo(
        standby = standby.toBooleanStrict(),
        muted = muted,
        volume = volume,
        currentServiceReference = currentServiceReference
    )
}