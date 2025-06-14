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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun Modifier.horizontalSafeContentPadding(useNavDrawer: Boolean = false): Modifier {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return this
        .consumeWindowInsets(
            if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
                windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT && useNavDrawer
            ) {
                WindowInsets.safeDrawing
                    .only(
                        WindowInsetsSides.End
                    )
            } else {
                WindowInsets.safeDrawing
                    .only(
                        WindowInsetsSides.Horizontal
                    )
            }
        )
        .padding(
            if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
                windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT && useNavDrawer
            ) {
                WindowInsets.safeDrawing
                    .only(
                        WindowInsetsSides.End
                    )
                    .asPaddingValues()
            } else {
                WindowInsets.safeDrawing
                    .only(
                        WindowInsetsSides.Horizontal
                    )
                    .asPaddingValues()
            }
        )
}