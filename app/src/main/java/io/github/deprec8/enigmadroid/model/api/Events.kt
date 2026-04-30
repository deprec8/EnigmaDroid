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
import io.github.deprec8.enigmadroid.common.enums.ContentFlag
import io.github.deprec8.enigmadroid.data.serialization.HtmlDecodedStringSerializer
import io.github.deprec8.enigmadroid.utils.TimestampUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Event(
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("sname") val serviceName: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("title") val title: String = "N/A",
    @SerialName("begin_timestamp") val beginTimestamp: Long = 0L,
    @SerialName("now_timestamp") val nowTimestamp: Long = 0L,
    @SerialName("sref") val serviceReference: String = "",
    @SerialName("id") val id: Int = 0,
    @SerialName("duration_sec") val durationInSeconds: Long = 0L,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("shortdesc") val shortDescription: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("genre") val genre: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("longdesc") val longDescription: String = "",
    val displayIndex: Int? = null,
    val flag: ContentFlag = ContentFlag.Channel
)

@Immutable
@Serializable
data class EventBatch(
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("bouquetName") val name: String = "N/A",
    @SerialName("events") val events: List<Event> = emptyList(),
)

@Immutable
data class EventBatchSet(
    val eventBatches: List<EventBatch> = emptyList()
)

suspend fun List<Event>.search(filter: String): List<Event>? {
    val events = this

    return withContext(Dispatchers.Default) {
        if (filter.isBlank() || events.isEmpty()) return@withContext null

        val filterTerms = filter.lowercase().split(" ").filter { it.isNotBlank() }

        events.filter { it.flag == ContentFlag.Channel }.asSequence().map { event ->
            val lcService = event.serviceName.lowercase()
            val lcTitle = event.title.lowercase()
            val lcLongDesc = event.longDescription.lowercase()
            val lcShortDesc = event.shortDescription.lowercase()
            val lcGenre = event.genre.lowercase()
            val lcBegin = TimestampUtils.formatApiTimestampToTime(event.beginTimestamp).lowercase()
            val lcEnd =
                TimestampUtils.formatApiTimestampToTime(event.beginTimestamp + event.durationInSeconds)
                    .lowercase()

            val matches = filterTerms.all { term ->
                lcService.contains(term) || lcTitle.contains(term) || lcLongDesc.contains(term) || lcShortDesc.contains(
                    term
                ) || lcGenre.contains(term) || lcBegin.contains(term) || lcEnd.contains(term)
            }

            val score = if (matches) {
                filterTerms.count { lcService.contains(it) } * 7 + filterTerms.count {
                    lcTitle.contains(
                        it
                    )
                } * 6 + filterTerms.count { lcLongDesc.contains(it) } * 5 + filterTerms.count {
                    lcShortDesc.contains(
                        it
                    )
                } * 4 + filterTerms.count { lcGenre.contains(it) } * 3 + filterTerms.count {
                    lcBegin.contains(
                        it
                    )
                } * 2 + filterTerms.count { lcEnd.contains(it) }
            } else 0

            Triple(event, matches, score)
        }.filter { it.second }.sortedByDescending { it.third }.map { it.first }.toList()
    }
}