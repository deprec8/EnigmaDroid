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
import io.github.deprec8.enigmadroid.utils.FuzzySearchUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Movie(
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("description") val shortDescription: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("tags") val tags: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("filename") val fileName: String = "",
    @SerialName("serviceref") val serviceReference: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("eventname") val eventName: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("length") val length: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("servicename") val serviceName: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("begintime") val begin: String = "N/A",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("descriptionExtended") val longDescription: String = "",
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("filesize_readable") val filesizeReadable: String = "N/A"
)

@Immutable
@Serializable
data class MovieBatch(
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("directory") val directory: String = "",
    @SerialName("movies") val movies: List<Movie> = emptyList(),
    @SerialName("bookmarks") val bookmarks: List<String> = emptyList()
)

suspend fun List<Movie>.search(filter: String): List<Movie>? {
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
            val nBegin = FuzzySearchUtils.normalize(movie.begin)
            val nEventName = FuzzySearchUtils.normalize(movie.eventName)

            val matches = filterTerms.all { term ->
                FuzzySearchUtils.fuzzyMatch(
                    nService, term
                ) || FuzzySearchUtils.fuzzyMatch(nLongDesc, term) || FuzzySearchUtils.fuzzyMatch(
                    nShortDesc, term
                ) || FuzzySearchUtils.fuzzyMatch(nTags, term) || FuzzySearchUtils.fuzzyMatch(
                    nBegin, term
                ) || FuzzySearchUtils.fuzzyMatch(nEventName, term)
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
                    ) * 3 + FuzzySearchUtils.calculateScore(
                        nBegin, term
                    ) * 2 + FuzzySearchUtils.calculateScore(nEventName, term) * 1
                }
            } else 0

            Triple(movie, matches, score)
        }.filter { it.second }.sortedByDescending { it.third }.map { it.first }.toList()
    }
}