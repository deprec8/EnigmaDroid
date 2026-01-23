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

package io.github.deprec8.enigmadroid.model.api.movies

import io.github.deprec8.enigmadroid.model.api.movies.bookmarks.Bookmark
import io.github.deprec8.enigmadroid.utils.HtmlDecodedStringSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieBatch(
    @Serializable(with = HtmlDecodedStringSerializer::class) @SerialName("directory") val directory: String = "",
    @Contextual @SerialName("bookmark") val bookmark: Bookmark = Bookmark(),
    @SerialName("movies") val movies: List<Movie> = emptyList(),
    @SerialName("bookmarks") val bookmarks: List<String> = emptyList(),
)