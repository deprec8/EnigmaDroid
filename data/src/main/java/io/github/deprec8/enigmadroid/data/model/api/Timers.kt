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
import io.github.deprec8.enigmadroid.data.common.toBoolean
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull

enum class AfterEventState(val value: Int) {
    Standby(1), DeepStandby(2), Auto(3)
}

enum class TimerState(val value: Int) {
    Waiting(0), Prepared(1), Running(2), Ended(3)
}

internal fun Int.toTimerState() = when (this) {
    0 -> TimerState.Waiting
    1 -> TimerState.Prepared
    2 -> TimerState.Running
    3 -> TimerState.Ended
    else -> throw IllegalArgumentException("The integer doesn't represent a timer state: $this")
}

internal fun Int.toAfterEventState() = when (this) {
    1 -> AfterEventState.Standby
    2 -> AfterEventState.DeepStandby
    3 -> AfterEventState.Auto
    else -> throw IllegalArgumentException("The integer doesn't represent an after event state: $this")
}

@Entity(
    tableName = "timers", primaryKeys = ["deviceId", "id"]
)
internal data class TimerEntity(
    val deviceId: Long,
    val id: Int,
    val serviceReference: String,
    val title: String,
    val shortDescription: String,
    val longDescription: String,
    val disabled: Boolean,
    val beginTimestamp: Long,
    val endTimestamp: Long,
    val justPlay: Boolean,
    val afterEventState: Int,
    val directoryName: String,
    val tags: String,
    val state: Int,
    val repeated: Int,
    val nextActivationTimestamp: Long?,
    val cancelled: Boolean
)

@Entity(
    tableName = "timers_log_entries", primaryKeys = ["deviceId", "id", "timestamp"]
)
internal data class LogEntryEntity(
    val deviceId: Long, val timerId: Int, val timestamp: Long, val code: Int, val message: String
)

@Serializable
internal data class TimerListDto(
    @SerialName("timers") val timers: List<TimerDto>
) {
    fun toTimerEntities(deviceId: Long) = timers.mapIndexed { index, timerDto ->
        TimerEntity(
            deviceId = deviceId,
            id = index,
            serviceReference = timerDto.serviceReference,
            title = timerDto.title,
            shortDescription = timerDto.shortDescription,
            longDescription = timerDto.longDescription,
            disabled = timerDto.disabled.toBoolean(),
            beginTimestamp = timerDto.beginTimestamp.times(1000),
            endTimestamp = timerDto.endTimestamp.times(1000),
            justPlay = timerDto.justPlay.toBoolean(),
            afterEventState = timerDto.afterEventState,
            directoryName = timerDto.directoryName,
            tags = timerDto.tags,
            state = timerDto.state,
            repeated = timerDto.repeated,
            nextActivationTimestamp = timerDto.nextActivationTimestamp?.times(1000),
            cancelled = timerDto.cancelled
        )
    }

    fun toLogEntryEntities(deviceId: Long) = timers.flatMapIndexed { index, timerDto ->
        timerDto.logEntries.map { logEntryDto ->
            LogEntryEntity(
                deviceId = deviceId,
                timerId = index,
                timestamp = logEntryDto.timestamp.times(1000),
                code = logEntryDto.code,
                message = logEntryDto.message
            )
        }
    }
}

@Serializable
internal data class TimerDto(
    @SerialName("serviceref") val serviceReference: String,

    @SerialName("eit") val eventId: Int = 0,

    @SerialName("name") val title: String = "N/A",

    @SerialName("description") val shortDescription: String = "",

    @SerialName("descriptionextended") val longDescription: String = "",

    @SerialName("disabled") val disabled: Int = 0,

    @SerialName("begin") val beginTimestamp: Long = 0L,

    @SerialName("end") val endTimestamp: Long = 0L,

    @SerialName("justplay") val justPlay: Int = 0,

    @SerialName("afterevent") val afterEventState: Int = 1,

    @SerialName("dirname") val directoryName: String = "",

    @SerialName("tags") val tags: String = "",

    @Serializable(with = LogEntrySerializer::class) @SerialName("logentries") val logEntries: List<LogEntryDto> = emptyList(),

    @SerialName("state") val state: Int = 0,

    @Serializable(with = LenientBooleanIntSerializer::class) @SerialName("repeated") val repeated: Int = 0,

    @SerialName("nextactivation") val nextActivationTimestamp: Long? = null,

    @SerialName("cancelled") val cancelled: Boolean = false
)

@Serializable
internal data class LogEntryDto(
    val timestamp: Long, val code: Int, val message: String
)

internal object LogEntrySerializer : KSerializer<List<LogEntryDto>> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = listSerialDescriptor(
        listSerialDescriptor(String.serializer().descriptor)
    )

    override fun deserialize(decoder: Decoder): List<LogEntryDto> {
        val rawList = decoder.decodeSerializableValue(
            ListSerializer(ListSerializer(String.serializer()))
        )

        return rawList.map { entryArray ->
            val timestampString =
                entryArray.getOrNull(0) ?: throw SerializationException("Missing timestamp")
            val codeString = entryArray.getOrNull(1) ?: throw SerializationException("Missing code")
            val message = entryArray.getOrNull(2) ?: throw SerializationException("Missing message")

            LogEntryDto(
                timestamp = timestampString.toLong().times(1000L),
                code = codeString.toInt(),
                message = message
            )
        }
    }

    override fun serialize(encoder: Encoder, value: List<LogEntryDto>) {
        val rawList = value.map { entry ->
            listOf(entry.timestamp.toString(), entry.code.toString(), entry.message)
        }
        encoder.encodeSerializableValue(
            ListSerializer(ListSerializer(String.serializer())), rawList
        )
    }
}

internal object LenientBooleanIntSerializer : KSerializer<Int> {
    override val descriptor = PrimitiveSerialDescriptor("LenientBooleanInt", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Invalid file format")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> when {
                element.booleanOrNull != null -> if (element.boolean) 127 else 0
                else -> element.intOrNull ?: error("Invalid value: $element")
            }

            else -> error("Invalid value: $element")
        }
    }

    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeInt(value)
    }
}