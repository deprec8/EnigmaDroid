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

import java.text.Normalizer
import kotlin.math.min

object FuzzySearchUtils {

    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    fun normalize(s: String): String {
        val temp = Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

    private fun damerauLevenshteinDistance(s1: String, s2: String): Int {
        if (s1 == s2) return 0
        if (s1.isEmpty()) return s2.length
        if (s2.isEmpty()) return s1.length

        val n = s1.length
        val m = s2.length
        val matrix = Array(n + 1) { IntArray(m + 1) }

        for (i in 0..n) matrix[i][0] = i
        for (j in 0..m) matrix[0][j] = j

        for (i in 1..n) {
            for (j in 1..m) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                matrix[i][j] = min(
                    min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1),
                    matrix[i - 1][j - 1] + cost
                )
                if (i > 1 && j > 1 && s1[i - 1] == s2[j - 2] && s1[i - 2] == s2[j - 1]) {
                    matrix[i][j] = min(matrix[i][j], matrix[i - 2][j - 2] + cost)
                }
            }
        }
        return matrix[n][m]
    }

    fun fuzzyMatch(normalizedText: String, normalizedQuery: String): Boolean {
        if (normalizedQuery.isEmpty()) return true
        if (normalizedText.contains(normalizedQuery)) return true

        val words = normalizedText.split(" ", ".", "-", "_").filter { it.isNotBlank() }
        val threshold = when {
            normalizedQuery.length <= 3 -> 0
            normalizedQuery.length <= 5 -> 1
            else -> 2
        }

        return words.any { word ->
            damerauLevenshteinDistance(word, normalizedQuery) <= threshold
        }
    }

    fun calculateScore(normalizedText: String, normalizedQuery: String): Int {
        if (normalizedQuery.isEmpty()) return 0

        if (normalizedText == normalizedQuery) return 100
        if (normalizedText.startsWith(normalizedQuery)) return 80
        if (normalizedText.contains(normalizedQuery)) return 60

        val words = normalizedText.split(" ", ".", "-", "_").filter { it.isNotBlank() }
        var maxFuzzyScore = 0

        val threshold = when {
            normalizedQuery.length <= 3 -> 0
            normalizedQuery.length <= 5 -> 1
            else -> 2
        }

        for (word in words) {
            val distance = damerauLevenshteinDistance(word, normalizedQuery)
            if (distance <= threshold) {
                val score = 40 - (distance * 10)
                if (score > maxFuzzyScore) maxFuzzyScore = score
            }
        }

        return maxFuzzyScore
    }
}