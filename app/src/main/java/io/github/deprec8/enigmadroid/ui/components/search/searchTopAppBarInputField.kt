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

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.ArrowNavigationButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBarInputField(
    searchBarState: SearchBarState,
    enabled: Boolean,
    textFieldState: TextFieldState,
    onSearch: () -> Unit,
    placeholder: String,
    navigationButton: @Composable ((searchBarState: SearchBarState) -> Unit),
    actionButtons: @Composable (() -> Unit)? = null
) {
    val isExpanded = searchBarState.currentValue == SearchBarValue.Expanded
    val scope = rememberCoroutineScope()

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
                text = placeholder, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            if (isExpanded) {
                ArrowNavigationButton {
                    scope.launch {
                        searchBarState.animateToCollapsed()
                    }
                }
            } else {
                navigationButton(searchBarState)
            }
        },
        trailingIcon = {
            if (isExpanded) {
                TooltipBox(
                    tooltip = {
                        PlainTooltip {
                            Text(stringResource(id = R.string.clear))
                        }
                    },
                    state = rememberTooltipState(),
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Below, 4.dp
                    )
                ) {
                    IconButton(onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd("")
                    }, enabled = textFieldState.text.isNotEmpty()) {
                        Icon(
                            Icons.Default.Clear, contentDescription = stringResource(R.string.clear)
                        )
                    }
                }
            } else {
                actionButtons?.invoke()
            }
        })
}