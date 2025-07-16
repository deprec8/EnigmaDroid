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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import io.github.deprec8.enigmadroid.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    input: String,
    onInputChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    placeholder: String,
    drawerState: DrawerState,
    onNavigateToRemote: () -> Unit,
    content: @Composable (() -> Unit)? = null,
    tabBar: @Composable (() -> Unit)? = null,
    onSearch: () -> Unit,
    enabled: Boolean = true
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val layoutDirection = LocalLayoutDirection.current

    val animatedEndPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else WindowInsets.safeDrawing.asPaddingValues()
            .calculateEndPadding(layoutDirection) + 16.dp, label = ""
    )
    val animatedStartPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else if (windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.EXPANDED ||
            windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
        ) WindowInsets.safeDrawing.asPaddingValues()
            .calculateStartPadding(layoutDirection) + 16.dp else 16.dp, label = ""
    )
    val animatedInputFieldStartPadding by animateDpAsState(
        targetValue = if (! expanded) 0.dp else if (windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.EXPANDED ||
            windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
        ) WindowInsets.safeDrawing.asPaddingValues()
            .calculateStartPadding(layoutDirection) else 0.dp, label = ""
    )
    val animatedInputFieldEndPadding by animateDpAsState(
        targetValue = if (! expanded) 0.dp else WindowInsets.safeDrawing.asPaddingValues()
            .calculateEndPadding(layoutDirection), label = ""
    )

    val animatedTopPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else WindowInsets.safeDrawing.asPaddingValues()
            .calculateTopPadding(), label = ""
    )
    val animatedInputFieldTopPadding by animateDpAsState(
        targetValue = if (! expanded) 0.dp else WindowInsets.safeDrawing.asPaddingValues()
            .calculateTopPadding(), label = ""
    )
    val scope = rememberCoroutineScope()

    Surface {
        Column {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        colors = TextFieldDefaults.colors(
                            disabledLeadingIconColor = TextFieldDefaults.colors().unfocusedLeadingIconColor,
                            disabledTrailingIconColor = TextFieldDefaults.colors().unfocusedTrailingIconColor
                        ),
                        enabled = enabled,
                        query = input,
                        onQueryChange = { onInputChange(it) },
                        onSearch = { onSearch() },
                        expanded = expanded,
                        onExpandedChange = { onExpandedChange(it) },
                        modifier = Modifier
                            .consumeWindowInsets(WindowInsets.safeDrawing)
                            .fillMaxWidth()
                            .padding(
                                start = animatedInputFieldStartPadding,
                                end = animatedInputFieldEndPadding,
                                top = animatedInputFieldTopPadding
                            ),
                        placeholder = {
                            Text(
                                text = placeholder,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            if (expanded) {
                                IconButton(onClick = { onExpandedChange(false) }) {
                                    Icon(
                                        Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = stringResource(
                                            R.string.close_search
                                        )
                                    )
                                }
                            } else if (
                                windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.EXPANDED ||
                                windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
                            ) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = stringResource(id = R.string.open_menu)
                                    )
                                }
                            } else {
                                IconButton(onClick = { onExpandedChange(true) }) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = stringResource(R.string.open_search)
                                    )
                                }
                            }
                        },
                        trailingIcon = {
                            if (expanded) {
                                IconButton(onClick = {
                                    onInputChange("")
                                }, enabled = input.isNotEmpty()) {
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
                },
                expanded = expanded,
                onExpandedChange = { onExpandedChange(it) },
                modifier = Modifier
                    .consumeWindowInsets(WindowInsets.safeDrawing)
                    .fillMaxWidth()
                    .padding(
                        start = animatedStartPadding,
                        end = animatedEndPadding,
                        top = animatedTopPadding
                    ),
                content = {
                    if (content != null) {
                        content()
                    } else {
                        NoResults()
                    }
                },
            )
            if (tabBar != null) {
                Column(Modifier.horizontalSafeContentPadding(true)) {
                    tabBar()
                }
            }

        }

    }

}