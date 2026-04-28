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
import io.github.deprec8.enigmadroid.data.serialization.LogEntrySerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Timer(
    @SerialName("begin") val beginTimestamp: Long = 0L,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("description") val shortDescription: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("tags") val tags: String = "",
    @SerialName("always_zap") val alwaysZap: Int = 0,
    @SerialName("disabled") val disabled: Int = 0,
    @SerialName("repeated") val repeated: Int = 0,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("servicename") val serviceName: String = "",
    @SerialName("duration") val durationInSeconds: Int = 0,
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("dirname") val directoryName: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("realend") val end: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("descriptionextended") val descriptionextended: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("name") val title: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("realbegin") val begin: String = "",
    @SerialName("end") val endTimestamp: Long = 0L,
    @SerialName("afterevent") val afterEvent: Int = 3,
    @SerialName("justplay") val justPlay: Int = 0,
    @SerialName("serviceref") val serviceReference: String = "",
    @SerialName("state") val state: Int = 0,
    @SerialName("cancelled") val cancelled: Boolean = false,
    @Serializable(with = LogEntrySerializer::class) @SerialName("logentries") val logEntries: List<LogEntry> = emptyList()
)

@Immutable
@Serializable
data class TimerBatch(
    @SerialName("timers") val timers: List<Timer> = emptyList()
)

@Immutable
@Serializable
data class LogEntry(
    val timestamp: Long, val code: Int, val message: String
)

suspend fun List<Timer>.search(filter: String): List<Timer>? {
    val timers = this

    return withContext(Dispatchers.Default) {
        if (filter.isBlank() || timers.isEmpty()) return@withContext null

        val filterTerms = filter.lowercase().split(" ").filter { it.isNotBlank() }

        return@withContext timers.asSequence().map { timer ->
            val lcTitle = timer.title.lowercase()
            val lcExtDesc = timer.descriptionextended.lowercase()
            val lcShortDesc = timer.shortDescription.lowercase()
            val lcService = timer.serviceName.lowercase()
            val lcTags = timer.tags.lowercase()
            val lcBegin = timer.begin.lowercase()
            val lcEnd = timer.end.lowercase()

            val matches = filterTerms.all { term ->
                lcTitle.contains(term) || lcExtDesc.contains(term) || lcShortDesc.contains(term) || lcService.contains(
                    term
                ) || lcTags.contains(term) || lcBegin.contains(term) || lcEnd.contains(term)
            }

            val score = if (matches) {
                filterTerms.count { lcTitle.contains(it) } * 7 + filterTerms.count {
                    lcExtDesc.contains(
                        it
                    )
                } * 6 + filterTerms.count { lcShortDesc.contains(it) } * 5 + filterTerms.count {
                    lcService.contains(
                        it
                    )
                } * 4 + filterTerms.count { lcTags.contains(it) } * 3 + filterTerms.count {
                    lcBegin.contains(
                        it
                    )
                } * 2 + filterTerms.count { lcEnd.contains(it) } * 1
            } else 0

            Triple(timer, matches, score)
        }.filter { it.second }.sortedByDescending { it.third }.map { it.first }.toList()
    }
}