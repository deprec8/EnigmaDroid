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
import io.github.deprec8.enigmadroid.utils.FuzzySearchUtils
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

        val filterTerms =
            filter.split(" ").filter { it.isNotBlank() }.map { FuzzySearchUtils.normalize(it) }

        events.filter { it.flag == ContentFlag.Channel }.asSequence().map { event ->
            val nService = FuzzySearchUtils.normalize(event.serviceName)
            val nTitle = FuzzySearchUtils.normalize(event.title)
            val nLongDesc = FuzzySearchUtils.normalize(event.longDescription)
            val nShortDesc = FuzzySearchUtils.normalize(event.shortDescription)
            val nGenre = FuzzySearchUtils.normalize(event.genre)
            val nBegin =
                FuzzySearchUtils.normalize(TimestampUtils.formatApiTimestampToTime(event.beginTimestamp))
            val nEnd =
                FuzzySearchUtils.normalize(TimestampUtils.formatApiTimestampToTime(event.beginTimestamp + event.durationInSeconds))

            val matches = filterTerms.all { term ->
                FuzzySearchUtils.fuzzyMatch(nService, term) || FuzzySearchUtils.fuzzyMatch(
                    nTitle, term
                ) || FuzzySearchUtils.fuzzyMatch(nLongDesc, term) || FuzzySearchUtils.fuzzyMatch(
                    nShortDesc, term
                ) || FuzzySearchUtils.fuzzyMatch(
                    nGenre, term
                ) || FuzzySearchUtils.fuzzyMatch(nBegin, term) || FuzzySearchUtils.fuzzyMatch(
                    nEnd, term
                )
            }

            val score = if (matches) {
                filterTerms.sumOf { term ->
                    FuzzySearchUtils.calculateScore(
                        nService, term
                    ) * 7 + FuzzySearchUtils.calculateScore(
                        nTitle, term
                    ) * 6 + FuzzySearchUtils.calculateScore(
                        nLongDesc, term
                    ) * 5 + FuzzySearchUtils.calculateScore(
                        nShortDesc, term
                    ) * 4 + FuzzySearchUtils.calculateScore(
                        nGenre, term
                    ) * 3 + FuzzySearchUtils.calculateScore(
                        nBegin, term
                    ) * 2 + FuzzySearchUtils.calculateScore(nEnd, term) * 1
                }
            } else 0

            Triple(event, matches, score)
        }.filter { it.second }.sortedByDescending { it.third }.map { it.first }.toList()
    }
}