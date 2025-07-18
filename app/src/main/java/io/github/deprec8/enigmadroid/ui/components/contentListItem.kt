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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.menu.MenuSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentListItem(
    headlineText: String,
    leadingContent: @Composable (() -> Unit)? = null,
    supportingText: String,
    overlineText: String? = null,
    additionalInfo: String? = null,
    menuSections: List<MenuSection>? = null,
    progress: Float? = null,
    shortDescription: String,
    longDescription: String,
    editMenuSection: MenuSection? = null,
    highlightedWords: List<String> = emptyList()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showDropDownMenu by rememberSaveable {
        mutableStateOf(false)
    }

    Column(Modifier.clickable { showBottomSheet = true }) {
        ListItem(
            headlineContent = {
                HighlightedText(
                    text = headlineText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    highlightedWords
                )
            },
            leadingContent = leadingContent,
            overlineContent = if (! overlineText.isNullOrEmpty()) {
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
            },
            supportingContent = {
                Column {
                    HighlightedText(
                        text = supportingText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        highlightedWords
                    )
                    if (! additionalInfo.isNullOrEmpty()) {
                        HighlightedText(
                            text = additionalInfo,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            highlightedWords
                        )
                    }
                }
            }, trailingContent = {
                if (! menuSections.isNullOrEmpty()) {
                    IconButton(onClick = { showDropDownMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(
                                R.string.open_action_menu
                            )
                        )
                        DropdownMenu(
                            expanded = showDropDownMenu,
                            onDismissRequest = { showDropDownMenu = false }) {
                            menuSections.forEachIndexed { index, menuSection ->
                                if (index != 0) {
                                    HorizontalDivider()
                                }
                                menuSection.menuItems.forEach { menuItem ->
                                    DropdownMenuItem(
                                        onClick = {
                                            showDropDownMenu = false
                                            menuItem.action()
                                        },
                                        text = {
                                            Text(
                                                text = menuItem.text,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                menuItem.outlinedIcon,
                                                contentDescription = stringResource(
                                                    R.string.open_action_menu
                                                )
                                            )
                                        }

                                    )
                                }
                            }
                            if (editMenuSection != null) {
                                HorizontalDivider()
                                editMenuSection.menuItems.forEach { menuItem ->
                                    DropdownMenuItem(
                                        onClick = {
                                            showDropDownMenu = false
                                            menuItem.action()
                                        },
                                        text = {
                                            Text(
                                                text = menuItem.text,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                menuItem.outlinedIcon,
                                                contentDescription = stringResource(
                                                    R.string.open_action_menu
                                                )
                                            )
                                        }

                                    )
                                }
                            }
                        }
                    }
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
        if (progress != null) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                strokeCap = StrokeCap.Round
            )
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            ContentDetails(
                headlineText = headlineText,
                leadingContent = leadingContent,
                supportingText = supportingText,
                additionalInfo = additionalInfo,
                overlineText = overlineText,
                progress = progress,
                shortDescription = shortDescription,
                longDescription = longDescription,
                menuSections = menuSections,
                editMenuSection = editMenuSection,
                highlightedWords = highlightedWords
            )
        }
    }
}