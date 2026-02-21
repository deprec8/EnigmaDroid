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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.insets.topAppBarWithDrawerWindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    textFieldState: TextFieldState,
    placeholder: String,
    navigationButton: @Composable ((searchBarState: SearchBarState) -> Unit),
    actionButtons: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
    tabBar: @Composable (() -> Unit)? = null,
    onSearch: () -> Unit,
    enabled: Boolean = true
) {

    val searchBarState = rememberSearchBarState()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val isSmallScreenLayout =
        ! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) || ! windowSizeClass.isHeightAtLeastBreakpoint(
            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
        )

    Surface {
        Column(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(topAppBarWithDrawerWindowInsets())
        ) {
            SearchBar(
                searchBarState,
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .then(if (! isSmallScreenLayout) Modifier.align(Alignment.End) else Modifier.fillMaxWidth()),
                inputField = {
                    SearchTopAppBarInputField(
                        searchBarState = searchBarState,
                        enabled = enabled,
                        textFieldState = textFieldState,
                        onSearch = onSearch,
                        placeholder = placeholder,
                        navigationButton = navigationButton,
                        actionButtons = actionButtons
                    )
                })
            if (isSmallScreenLayout) {
                ExpandedFullScreenSearchBar(
                    state = searchBarState, inputField = {
                        SearchTopAppBarInputField(
                            searchBarState = searchBarState,
                            enabled = enabled,
                            textFieldState = textFieldState,
                            onSearch = onSearch,
                            placeholder = placeholder,
                            navigationButton = navigationButton,
                            actionButtons = actionButtons
                        )
                    }) {
                    if (content != null && enabled) {
                        content()
                    } else {
                        NoResults()
                    }
                }
            } else {
                ExpandedDockedSearchBar(
                    state = searchBarState, {
                        SearchTopAppBarInputField(
                            searchBarState = searchBarState,
                            enabled = enabled,
                            textFieldState = textFieldState,
                            onSearch = onSearch,
                            placeholder = placeholder,
                            navigationButton = navigationButton,
                            actionButtons = actionButtons
                        )
                    }) {
                    if (content != null && enabled) {
                        content()
                    } else {
                        NoResults()
                    }
                }
            }
            if (tabBar != null) {
                Column(Modifier.padding(top = 8.dp)) {
                    tabBar()
                }
            } else {
                Spacer(Modifier.size(8.dp))
            }
        }
    }
}