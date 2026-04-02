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

package io.github.deprec8.enigmadroid.ui.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.menu.MenuItemGroup
import io.github.deprec8.enigmadroid.ui.components.search.HighlightedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDetails(
    headlineText: String,
    leadingContent: @Composable (() -> Unit)? = null,
    supportingText: String,
    overlineText: String? = null,
    additionalInfo: String? = null,
    menuItemGroups: List<MenuItemGroup>? = null,
    progress: Float? = null,
    shortDescription: String,
    longDescription: String,
    editMenuItemGroup: MenuItemGroup? = null,
    highlightedWords: List<String> = emptyList(),
    onHideBottomSheet: () -> Unit,
    additionalDescription: String? = null,
) {

    var showDropDownMenu by rememberSaveable { mutableStateOf(false) }

    Column {
        OutlinedCard(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            ListItem(
                headlineContent = {
                    HighlightedText(
                        text = headlineText, highlightedWords = highlightedWords
                    )
                }, leadingContent = leadingContent, overlineContent = if (overlineText != null) {
                    {
                        HighlightedText(
                            text = overlineText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            highlightedWords
                        )
                    }
                } else {
                    null
                }, supportingContent = {
                    Column {
                        HighlightedText(
                            text = supportingText, highlightedWords = highlightedWords
                        )
                        if (additionalInfo != null) {
                            HighlightedText(
                                text = additionalInfo, highlightedWords = highlightedWords
                            )
                        }
                    }
                }, trailingContent = if (editMenuItemGroup != null) {
                    {
                        TooltipBox(
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(id = R.string.editing_menu))
                                }
                            },
                            state = rememberTooltipState(),
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above, 4.dp
                            )
                        ) {
                            FilledTonalIconButton(onClick = { showDropDownMenu = true }) {
                                Icon(
                                    Icons.Filled.EditNote,
                                    contentDescription = stringResource(R.string.editing_menu)
                                )
                                DropdownMenu(
                                    expanded = showDropDownMenu,
                                    onDismissRequest = { showDropDownMenu = false }) {
                                    editMenuItemGroup.menuItems.forEach { menuItem ->
                                        DropdownMenuItem(onClick = {
                                            showDropDownMenu = false
                                            onHideBottomSheet()
                                            menuItem.action()
                                        }, text = {
                                            Text(
                                                text = menuItem.text,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }, leadingIcon = {
                                            Icon(
                                                menuItem.outlinedIcon, contentDescription = null
                                            )
                                        })
                                    }
                                }
                            }
                        }
                    }
                } else {
                    null
                }, colors = ListItemDefaults.colors(containerColor = Color.Transparent))
            if (progress != null) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
        if (! menuItemGroups.isNullOrEmpty()) {
            Spacer(modifier = Modifier.size(16.dp))
            menuItemGroups.forEachIndexed { index, menuSection ->
                if (index != 0) {
                    Spacer(Modifier.size(8.dp))
                }
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    menuSection.menuItems.forEach { menuItem ->
                        FilledTonalButton(
                            modifier = Modifier.weight(1f),
                            content = {
                                Icon(
                                    imageVector = menuItem.filledIcon, contentDescription = null
                                )
                                Spacer(Modifier.size(12.dp))
                                Text(
                                    text = menuItem.text,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            onClick = {
                                onHideBottomSheet()
                                menuItem.action()
                            },
                        )
                    }
                }
            }
        }

        if (shortDescription.isNotBlank() || longDescription.isNotBlank() || ! additionalDescription.isNullOrBlank()) {
            Spacer(modifier = Modifier.size(16.dp))
            SelectionContainer {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (shortDescription.isNotBlank()) {
                        item {
                            HighlightedText(
                                text = shortDescription, highlightedWords = highlightedWords
                            )
                        }
                    }
                    if (shortDescription.isNotBlank() && longDescription.isNotBlank()) {
                        item {
                            HorizontalDivider()
                        }
                    }
                    if (longDescription.isNotBlank()) {
                        item {
                            HighlightedText(
                                text = longDescription, highlightedWords = highlightedWords
                            )
                        }
                    }
                    if ((shortDescription.isNotBlank() || longDescription.isNotBlank()) && ! additionalDescription.isNullOrBlank()) {
                        item {
                            HorizontalDivider()
                        }
                    }
                    if (! additionalDescription.isNullOrBlank()) {
                        item {
                            HighlightedText(
                                text = additionalDescription, highlightedWords = highlightedWords
                            )
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}