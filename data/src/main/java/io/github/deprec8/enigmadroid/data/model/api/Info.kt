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

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import androidx.room3.Relation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class SignalInfo(
    val agc: Double,
    val tunerNumber: Int,
    val snr: Double,
    val tunerType: String,
)

data class StatusInfo(
    val standby: Boolean, val muted: Boolean, val volume: Int, val currentServiceReference: String
)

@Entity(tableName = "device_infos")
data class DeviceInfoCore(
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
)

@Entity(
    tableName = "hdds", primaryKeys = ["deviceId", "mountDirectory"], foreignKeys = [ForeignKey(
        entity = DeviceInfoCore::class,
        parentColumns = ["deviceId"],
        childColumns = ["deviceId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Hdd(
    val deviceId: Long,
    val mountDirectory: String,
    val capacity: String,
    val freeSpace: String,
    val model: String
)

@Entity(
    tableName = "interfaces", primaryKeys = ["deviceId", "ip"], foreignKeys = [ForeignKey(
        entity = DeviceInfoCore::class,
        parentColumns = ["deviceId"],
        childColumns = ["deviceId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Interface(
    val deviceId: Long,
    val ip: String,
    val name: String,
    val friendlyNic: String,
    val gateway: String,
    val linkSpeed: String,
    val firstPublicIpv6: String,
    val ipv4Method: String
)

@Entity(
    tableName = "tuners", primaryKeys = ["deviceId", "position"], foreignKeys = [ForeignKey(
        entity = DeviceInfoCore::class,
        parentColumns = ["deviceId"],
        childColumns = ["deviceId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Tuner(
    val deviceId: Long, val position: Int, val type: String, val name: String
)

data class DeviceInfo(
    @Embedded val deviceInfo: DeviceInfoCore, @Relation(
        parentColumns = ["deviceId"], entityColumns = ["deviceId"]
    ) val hdds: List<Hdd>, @Relation(
        parentColumns = ["deviceId"], entityColumns = ["deviceId"]
    ) val interfaces: List<Interface>, @Relation(
        parentColumns = ["deviceId"], entityColumns = ["deviceId"]
    ) val tuners: List<Tuner>
)

@Serializable
internal data class NetworkSignalInfo(
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
internal data class NetworkStatusInfo(
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

@Serializable
internal data class NetworkDeviceInfo(
    @SerialName("tuners") val tuners: List<NetworkTuner>,
    @SerialName("ifaces") val interfaces: List<NetworkInterface> = emptyList(),
    @SerialName("hdd") val hdds: List<NetworkHdd> = emptyList(),
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
    fun toDeviceInfo(deviceId: Long) = DeviceInfo(
        deviceInfo = DeviceInfoCore(
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
        ), hdds = hdds.map { hddDto ->
            Hdd(
                deviceId = deviceId,
                mountDirectory = hddDto.mountDirectory,
                capacity = hddDto.capacity,
                freeSpace = hddDto.freeSpace,
                model = hddDto.model
            )
        }, interfaces = interfaces.map { interfaceDto ->
            Interface(
                deviceId = deviceId,
                ip = interfaceDto.ip,
                name = interfaceDto.name,
                friendlyNic = interfaceDto.friendlyNic,
                gateway = interfaceDto.gateway,
                linkSpeed = interfaceDto.linkSpeed,
                firstPublicIpv6 = interfaceDto.firstPublicIpv6,
                ipv4Method = interfaceDto.ipv4Method,
            )
        }, tuners = tuners.mapIndexed { index, tunerDto ->
            Tuner(
                deviceId = deviceId, position = index, type = tunerDto.type, name = tunerDto.name
            )
        })
}

@Serializable
internal data class NetworkHdd(
    @SerialName("capacity") val capacity: String = "N/A",
    @SerialName("mount") val mountDirectory: String = "N/A",
    @SerialName("free") val freeSpace: String = "N/A",
    @SerialName("model") val model: String = "N/A"
)

@Serializable
internal data class NetworkInterface(
    @SerialName("ip") val ip: String = "N/A",
    @SerialName("name") val name: String = "N/A",
    @SerialName("friendlynic") val friendlyNic: String = "N/A",
    @SerialName("gw") val gateway: String = "N/A",
    @SerialName("linkspeed") val linkSpeed: String = "N/A",
    @SerialName("firstpublic") val firstPublicIpv6: String = "N/A",
    @SerialName("ipv4method") val ipv4Method: String = "N/A"
)

@Serializable
internal data class NetworkTuner(
    @SerialName("type") val type: String = "N/A", @SerialName("name") val name: String = "N/A"
)