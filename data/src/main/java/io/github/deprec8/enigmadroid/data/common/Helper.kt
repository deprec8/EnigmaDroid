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

package io.github.deprec8.enigmadroid.data.common

import org.jsoup.parser.Parser

fun String.decodeHtml() = Parser.unescapeEntities(this, false).map { ch ->
    if (ch.code in 0x80..0x9F) ' ' else ch
}.joinToString("")

fun Int.toBoolean() = if (this == 1) {
    true
} else if (this == 0) {
    false
} else {
    throw IllegalArgumentException("The integer doesn't represent a boolean value: $this")
}