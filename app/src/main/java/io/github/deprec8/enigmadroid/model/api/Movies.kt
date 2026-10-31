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

        val filterTerms = filter.lowercase().split(" ").filter { it.isNotBlank() }

        return@withContext movies.asSequence().map { movie ->
            val lcService = movie.serviceName.lowercase()
            val lcLongDesc = movie.longDescription.lowercase()
            val lcShortDesc = movie.shortDescription.lowercase()
            val lcTags = movie.tags.lowercase()
            val lcBegin = movie.begin.lowercase()
            val lcEventName = movie.eventName.lowercase()

            val matches = filterTerms.all { term ->
                lcService.contains(term) || lcLongDesc.contains(term) || lcShortDesc.contains(
                    term
                ) || lcTags.contains(term) || lcBegin.contains(term) || lcEventName.contains(
                    term
                )
            }

            val score = if (matches) {
                filterTerms.count { lcService.contains(it) } * 6 + filterTerms.count {
                    lcLongDesc.contains(
                        it
                    )
                } * 5 + filterTerms.count { lcShortDesc.contains(it) } * 4 + filterTerms.count {
                    lcTags.contains(
                        it
                    )
                } * 3 + filterTerms.count { lcBegin.contains(it) } * 2 + filterTerms.count {
                    lcEventName.contains(
                        it
                    )
                } * 1
            } else 0

            Triple(movie, matches, score)
        }.filter { it.second }.sortedByDescending { it.third }.map { it.first }.toList()
    }
}