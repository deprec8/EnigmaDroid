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

package io.github.deprec8.enigmadroid.ui.components.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import io.github.deprec8.enigmadroid.utils.FuzzySearchUtils

@Composable
fun HighlightedText(
    text: String,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    highlightedWords: List<String>
) {
    val highlightColor = MaterialTheme.colorScheme.primary

    val annotatedString = remember(text, highlightedWords) {
        if (highlightedWords.isNotEmpty()) {
            buildAnnotatedString {
                val words = text.split(Regex("(?<=\\W)|(?=\\W)"))
                for (word in words) {
                    val normalizedWord = FuzzySearchUtils.normalize(word)
                    if (word.isNotEmpty() && highlightedWords.any { term ->
                            FuzzySearchUtils.fuzzyMatch(normalizedWord, term)
                        }) {
                        withStyle(
                            SpanStyle(
                                color = highlightColor, fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(word)
                        }
                    } else {
                        append(word)
                    }
                }
            }
        } else {
            AnnotatedString(text)
        }
    }

    Text(
        text = annotatedString, maxLines = maxLines, overflow = overflow
    )
}