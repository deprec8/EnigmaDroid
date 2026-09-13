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

package io.github.deprec8.enigmadroid.data.temp

import io.github.deprec8.enigmadroid.common.enums.ContentFlag
import io.github.deprec8.enigmadroid.core.common.utils.FuzzySearchUtils
import io.github.deprec8.enigmadroid.core.common.utils.TimestampUtils
import io.github.deprec8.enigmadroid.core.network.model.NetworkEvent
import io.github.deprec8.enigmadroid.core.network.model.NetworkMovieList
import io.github.deprec8.enigmadroid.core.network.model.NetworkTimerBatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.String
import kotlin.Triple
import kotlin.collections.List
import kotlin.collections.all
import kotlin.collections.asSequence
import kotlin.collections.filter
import kotlin.collections.first
import kotlin.collections.map
import kotlin.collections.plus
import kotlin.collections.sortedByDescending
import kotlin.collections.sumOf
import kotlin.collections.toList
import kotlin.map
import kotlin.plus
import kotlin.ranges.first
import kotlin.sequences.first
import kotlin.sequences.map
import kotlin.sequences.plus
import kotlin.sequences.sortedByDescending
import kotlin.sequences.toList
import kotlin.text.first
import kotlin.text.isBlank
import kotlin.text.isNotBlank
import kotlin.text.map
import kotlin.text.plus
import kotlin.text.split
import kotlin.text.toList
import kotlin.times
import kotlin.toList

suspend fun List<NetworkMovieList.Movie>.search(filter: String): List<NetworkMovieList.Movie>? {
    val movies = this

    return withContext(Dispatchers.Default) {
        if (filter.isBlank() || movies.isEmpty()) return@withContext null

        val filterTerms =
            filter.split(" ").filter { it.isNotBlank() }.map { FuzzySearchUtils.normalize(it) }

        return@withContext movies.asSequence().map { movie ->
            val nService = FuzzySearchUtils.normalize(movie.serviceName)
            val nLongDesc = FuzzySearchUtils.normalize(movie.longDescription)
            val nShortDesc = FuzzySearchUtils.normalize(movie.shortDescription)
            val nTags = FuzzySearchUtils.normalize(movie.tags)
            val nEventName = FuzzySearchUtils.normalize(movie.eventName)

            val matches = filterTerms.all { term ->
                FuzzySearchUtils.fuzzyMatch(
                    nService, term
                ) || FuzzySearchUtils.fuzzyMatch(nLongDesc, term) || FuzzySearchUtils.fuzzyMatch(
                    nShortDesc, term
                ) || FuzzySearchUtils.fuzzyMatch(nTags, term) || FuzzySearchUtils.fuzzyMatch(
                    nEventName,
                    term
                )
            }

            val score = if (matches) {
                filterTerms.sumOf { term ->
                    FuzzySearchUtils.calculateScore(
                        nService, term
                    ) * 6 + FuzzySearchUtils.calculateScore(
                        nLongDesc, term
                    ) * 5 + FuzzySearchUtils.calculateScore(
                        nShortDesc, term
                    ) * 4 + FuzzySearchUtils.calculateScore(
                        nTags, term
                    ) * 3 + FuzzySearchUtils.calculateScore(nEventName, term) * 1
                }
            } else 0

            Triple(movie, matches, score)
        }.filter { it.second }.sortedByDescending { it.third }.map { it.first }.toList()
    }
}

suspend fun List<NetworkEvent>.search(filter: String): List<NetworkEvent>? {
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

suspend fun List<NetworkTimerBatch.Timer>.search(filter: String): List<NetworkTimerBatch.Timer>? {
    val timers = this

    return withContext(Dispatchers.Default) {
        if (filter.isBlank() || timers.isEmpty()) return@withContext null

        val filterTerms =
            filter.split(" ").filter { it.isNotBlank() }.map { FuzzySearchUtils.normalize(it) }

        return@withContext timers.asSequence().map { timer ->
            val nTitle = FuzzySearchUtils.normalize(timer.title)
            val nExtDesc = FuzzySearchUtils.normalize(timer.descriptionextended)
            val nShortDesc = FuzzySearchUtils.normalize(timer.shortDescription)
            val nService = FuzzySearchUtils.normalize(timer.serviceName)
            val nTags = FuzzySearchUtils.normalize(timer.tags)
            val nBegin = FuzzySearchUtils.normalize(timer.begin)
            val nEnd = FuzzySearchUtils.normalize(timer.end)

            val matches = filterTerms.all { term ->
                FuzzySearchUtils.fuzzyMatch(nTitle, term) || FuzzySearchUtils.fuzzyMatch(
                    nExtDesc, term
                ) || FuzzySearchUtils.fuzzyMatch(nShortDesc, term) || FuzzySearchUtils.fuzzyMatch(
                    nService, term
                ) || FuzzySearchUtils.fuzzyMatch(nTags, term) || FuzzySearchUtils.fuzzyMatch(
                    nBegin, term
                ) || FuzzySearchUtils.fuzzyMatch(nEnd, term)
            }

            val score = if (matches) {
                filterTerms.sumOf { term ->
                    FuzzySearchUtils.calculateScore(
                        nTitle, term
                    ) * 7 + FuzzySearchUtils.calculateScore(
                        nExtDesc, term
                    ) * 6 + FuzzySearchUtils.calculateScore(
                        nShortDesc, term
                    ) * 5 + FuzzySearchUtils.calculateScore(
                        nService, term
                    ) * 4 + FuzzySearchUtils.calculateScore(
                        nTags, term
                    ) * 3 + FuzzySearchUtils.calculateScore(
                        nBegin, term
                    ) * 2 + FuzzySearchUtils.calculateScore(nEnd, term) * 1
                }
            } else 0

            Triple(timer, matches, score)
        }.filter { it.second }.sortedByDescending { it.third }.map { it.first }.toList()
    }
}