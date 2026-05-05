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

package io.github.deprec8.enigmadroid.ui.components.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay

@Composable
fun sharedAxisXTransition(): Map<String, Any> {
    val density = LocalDensity.current
    val slideDistance = remember(density) { with(density) { 30.dp.roundToPx() } }

    val sharedAxisXForward: ContentTransform = (slideInHorizontally(
        animationSpec = tween(300), initialOffsetX = { slideDistance }) + fadeIn(
        animationSpec = tween(300)
    )) togetherWith (slideOutHorizontally(
        animationSpec = tween(300), targetOffsetX = { -slideDistance }) + fadeOut(
        animationSpec = tween(300)
    ))

    val sharedAxisXBackward: ContentTransform = (slideInHorizontally(
        animationSpec = tween(300), initialOffsetX = { -slideDistance }) + fadeIn(
        animationSpec = tween(300)
    )) togetherWith (slideOutHorizontally(
        animationSpec = tween(300), targetOffsetX = { slideDistance }) + fadeOut(
        animationSpec = tween(300)
    ))

    return metadata {
        put(NavDisplay.TransitionKey) {
            sharedAxisXForward
        }
        put(NavDisplay.PopTransitionKey) {
            sharedAxisXBackward
        }
        put(NavDisplay.PredictivePopTransitionKey) {
            sharedAxisXBackward
        }
    }
}

fun fadeThroughTransition(): Map<String, Any> {
    val fadeThrough: ContentTransform =
        (fadeIn(animationSpec = tween(300))) togetherWith (fadeOut(animationSpec = tween(300)))

    return metadata {
        put(NavDisplay.TransitionKey) {
            fadeThrough
        }
        put(NavDisplay.PopTransitionKey) {
            fadeThrough
        }
        put(NavDisplay.PredictivePopTransitionKey) {
            fadeThrough
        }
    }
}