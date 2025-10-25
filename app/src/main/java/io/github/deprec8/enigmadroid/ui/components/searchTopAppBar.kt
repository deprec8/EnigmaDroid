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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    textFieldState: TextFieldState,
    placeholder: String,
    drawerState: DrawerState,
    onNavigateToRemote: () -> Unit,
    content: @Composable (() -> Unit)? = null,
    tabBar: @Composable (() -> Unit)? = null,
    onSearch: () -> Unit,
    enabled: Boolean = true
) {

    val searchBarState = rememberSearchBarState()

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val scope = rememberCoroutineScope()

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            searchBarState = searchBarState,
            colors = SearchBarDefaults.inputFieldColors(
                disabledLeadingIconColor = TextFieldDefaults.colors().unfocusedLeadingIconColor,
                disabledTrailingIconColor = TextFieldDefaults.colors().unfocusedTrailingIconColor
            ),
            enabled = enabled,
            textFieldState = textFieldState,
            onSearch = { onSearch() },
            placeholder = {
                Text(
                    text = placeholder,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(onClick = {
                        scope.launch {
                            searchBarState.animateToCollapsed()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(
                                R.string.close_search
                            )
                        )
                    }
                } else if (
                    ! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ||
                    ! windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = stringResource(id = R.string.open_menu)
                        )
                    }
                } else {
                    IconButton(onClick = {
                        scope.launch {
                            searchBarState.animateToExpanded()
                        }
                    }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.open_search)
                        )
                    }
                }
            },
            trailingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd("")
                    }, enabled = textFieldState.text.isNotEmpty()) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_search)
                        )
                    }
                } else {
                    IconButton(onClick = { onNavigateToRemote() }) {
                        Icon(
                            Icons.Default.Dialpad,
                            contentDescription = stringResource(id = R.string.open_remote_control)
                        )
                    }
                }
            }
        )
    }

    Surface {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top).asPaddingValues())
                .horizontalSafeContentPadding(true)
        ) {
            SearchBar(
                searchBarState,
                modifier = if (! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ||
                    ! windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
                ) Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                else Modifier
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .align(
                        Alignment.End
                    ),
                inputField = inputField
            )
            if (! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ||
                ! windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
            ) {
                ExpandedFullScreenSearchBar(state = searchBarState, inputField) {
                    if (content != null && enabled) {
                        content()
                    } else {
                        NoResults()
                    }
                }
            } else {
                ExpandedDockedSearchBar(
                    state = searchBarState, inputField
                ) {
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
            }
        }
    }
}