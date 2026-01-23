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

package io.github.deprec8.enigmadroid.utils

import io.github.deprec8.enigmadroid.model.api.events.Event
import io.github.deprec8.enigmadroid.model.api.movies.Movie
import io.github.deprec8.enigmadroid.model.api.timers.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FilterUtils {

    suspend fun filterMovies(
        filter: String,
        movies: List<Movie>
    ): List<Movie> = withContext(Dispatchers.Default) {
        if (filter.isBlank() || movies.isEmpty()) return@withContext emptyList()

        val filterTerms = filter.lowercase().split(" ").filter { it.isNotBlank() }

        movies
            .asSequence()
            .map { movie ->
                val lcService = movie.serviceName.lowercase()
                val lcLongDesc = movie.longDescription.lowercase()
                val lcShortDesc = movie.shortDescription.lowercase()
                val lcTags = movie.tags.lowercase()
                val lcBegin = movie.begin.lowercase()
                val lcEventName = movie.eventName.lowercase()

                val matches = filterTerms.all { term ->
                    lcService.contains(term) ||
                            lcLongDesc.contains(term) ||
                            lcShortDesc.contains(term) ||
                            lcTags.contains(term) ||
                            lcBegin.contains(term) ||
                            lcEventName.contains(term)
                }

                val score = if (matches) {
                    filterTerms.count { lcService.contains(it) } * 6 +
                            filterTerms.count { lcLongDesc.contains(it) } * 5 +
                            filterTerms.count { lcShortDesc.contains(it) } * 4 +
                            filterTerms.count { lcTags.contains(it) } * 3 +
                            filterTerms.count { lcBegin.contains(it) } * 2 +
                            filterTerms.count { lcEventName.contains(it) } * 1
                } else 0

                Triple(movie, matches, score)
            }
            .filter { it.second }
            .sortedByDescending { it.third }
            .map { it.first }
            .toList()
    }

    suspend fun filterTimers(
        filter: String,
        timers: List<Timer>
    ): List<Timer> = withContext(Dispatchers.Default) {
        if (filter.isBlank() || timers.isEmpty()) return@withContext emptyList()

        val filterTerms = filter.lowercase().split(" ").filter { it.isNotBlank() }

        timers
            .asSequence()
            .map { timer ->
                val lcTitle = timer.title.lowercase()
                val lcExtDesc = timer.descriptionextended.lowercase()
                val lcShortDesc = timer.shortDescription.lowercase()
                val lcService = timer.serviceName.lowercase()
                val lcTags = timer.tags.lowercase()
                val lcBegin = timer.begin.lowercase()
                val lcEnd = timer.end.lowercase()

                val matches = filterTerms.all { term ->
                    lcTitle.contains(term) ||
                            lcExtDesc.contains(term) ||
                            lcShortDesc.contains(term) ||
                            lcService.contains(term) ||
                            lcTags.contains(term) ||
                            lcBegin.contains(term) ||
                            lcEnd.contains(term)
                }

                val score = if (matches) {
                    filterTerms.count { lcTitle.contains(it) } * 7 +
                            filterTerms.count { lcExtDesc.contains(it) } * 6 +
                            filterTerms.count { lcShortDesc.contains(it) } * 5 +
                            filterTerms.count { lcService.contains(it) } * 4 +
                            filterTerms.count { lcTags.contains(it) } * 3 +
                            filterTerms.count { lcBegin.contains(it) } * 2 +
                            filterTerms.count { lcEnd.contains(it) } * 1
                } else 0

                Triple(timer, matches, score)
            }
            .filter { it.second }
            .sortedByDescending { it.third }
            .map { it.first }
            .toList()
    }


    suspend fun filterEvents(
        filter: String,
        events: List<Event>
    ): List<Event> = withContext(Dispatchers.Default) {
        if (filter.isBlank() || events.isEmpty()) return@withContext emptyList()

        val filterTerms = filter.lowercase().split(" ").filter { it.isNotBlank() }

        events
            .asSequence()
            .map { event ->
                val lcService = event.serviceName.lowercase()
                val lcTitle = event.title.lowercase()
                val lcLongDesc = event.longDescription.lowercase()
                val lcShortDesc = event.shortDescription.lowercase()
                val lcGenre = event.genre.lowercase()
                val lcBegin =
                    TimestampUtils.formatApiTimestampToTime(event.beginTimestamp).lowercase()
                val lcEnd = TimestampUtils
                    .formatApiTimestampToTime(event.beginTimestamp + event.durationInSeconds)
                    .lowercase()

                val matches = filterTerms.all { term ->
                    lcService.contains(term) ||
                            lcTitle.contains(term) ||
                            lcLongDesc.contains(term) ||
                            lcShortDesc.contains(term) ||
                            lcGenre.contains(term) ||
                            lcBegin.contains(term) ||
                            lcEnd.contains(term)
                }

                val score = if (matches) {
                    filterTerms.count { lcService.contains(it) } * 7 +
                            filterTerms.count { lcTitle.contains(it) } * 6 +
                            filterTerms.count { lcLongDesc.contains(it) } * 5 +
                            filterTerms.count { lcShortDesc.contains(it) } * 4 +
                            filterTerms.count { lcGenre.contains(it) } * 3 +
                            filterTerms.count { lcBegin.contains(it) } * 2 +
                            filterTerms.count { lcEnd.contains(it) }
                } else 0

                Triple(event, matches, score)
            }
            .filter { it.second }
            .sortedByDescending { it.third }
            .map { it.first }
            .toList()
    }
}