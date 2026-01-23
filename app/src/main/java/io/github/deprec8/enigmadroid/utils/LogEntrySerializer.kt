/*
 * Copyright (C) 2026 deprec8
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

import io.github.deprec8.enigmadroid.model.api.timers.LogEntry
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LogEntrySerializer : KSerializer<List<LogEntry>> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = listSerialDescriptor(
        listSerialDescriptor(String.serializer().descriptor)
    )

    override fun deserialize(decoder: Decoder): List<LogEntry> {
        val rawList = decoder.decodeSerializableValue(
            ListSerializer(ListSerializer(String.serializer()))
        )

        return rawList.map { entryArray ->
            val timestampString =
                entryArray.getOrNull(0) ?: throw IllegalStateException("Missing timestamp")
            val codeString = entryArray.getOrNull(1) ?: throw IllegalStateException("Missing code")
            val message = entryArray.getOrNull(2) ?: throw IllegalStateException("Missing message")

            LogEntry(
                timestamp = timestampString.toLong(),
                code = codeString.toInt(),
                message = message
            )
        }
    }

    override fun serialize(encoder: Encoder, value: List<LogEntry>) {
        val rawList = value.map { entry ->
            listOf(entry.timestamp.toString(), entry.code.toString(), entry.message)
        }
        encoder.encodeSerializableValue(
            ListSerializer(ListSerializer(String.serializer())), rawList
        )
    }
}