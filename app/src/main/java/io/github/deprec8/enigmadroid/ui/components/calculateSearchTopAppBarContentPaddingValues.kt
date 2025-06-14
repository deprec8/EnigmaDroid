/*
 * Copyright (C) 2025 deprec8
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

package io.github.deprec8.enigmadroid.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun calculateSearchTopAppBarContentPaddingValues(): PaddingValues {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        top = 0.dp,
        start = if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
            windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT
        ) 0.dp else WindowInsets.safeDrawing.asPaddingValues()
            .calculateStartPadding(layoutDirection),
        end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(layoutDirection),
        bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
    )
}