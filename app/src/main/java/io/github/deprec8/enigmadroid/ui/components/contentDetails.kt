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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.menu.MenuSection

@Composable
fun ContentDetails(
    headlineText: String,
    leadingContent: @Composable (() -> Unit)? = null,
    supportingText: String,
    overlineText: String? = null,
    additionalInfo: String? = null,
    menuSections: List<MenuSection>? = null,
    progress: Float? = null,
    shortDescription: String,
    longDescription: String,
    editMenuSection: MenuSection? = null
) {

    var showDropDownMenu by rememberSaveable { mutableStateOf(false) }

    Column {
        ElevatedCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = headlineText,
                    )
                },
                leadingContent = leadingContent,
                overlineContent = if (overlineText != null) {
                    {
                        Text(
                            text = overlineText,

                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    null
                },
                supportingContent = {
                    Column {
                        Text(
                            text = supportingText
                        )
                        if (additionalInfo != null) {
                            Text(
                                text = additionalInfo
                            )
                        }
                    }
                },
                trailingContent = if (editMenuSection != null) {
                    {
                        OutlinedIconButton(onClick = { showDropDownMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(
                                    R.string.open_action_menu
                                )
                            )
                            DropdownMenu(
                                expanded = showDropDownMenu,
                                onDismissRequest = { showDropDownMenu = false }) {
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
                } else {
                    null
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer)
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
        if (! menuSections.isNullOrEmpty()) {
            Spacer(modifier = Modifier.size(16.dp))
            menuSections.forEachIndexed { index, menuSection ->
                if (index != 0) {
                    Spacer(Modifier.size(8.dp))
                }
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    menuSection.menuItems.forEach { menuItem ->
                        when (menuItem.text) {
                            stringResource(R.string.stream) -> Button(
                                modifier = Modifier.weight(1f),
                                content = {
                                    Icon(
                                        imageVector = menuItem.filledIcon,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.size(12.dp))
                                    Text(
                                        text = menuItem.text,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    menuItem.action()
                                },
                            )
                            else                            -> OutlinedButton(
                                modifier = Modifier.weight(1f),
                                content = {
                                    Icon(
                                        imageVector = menuItem.filledIcon,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.size(12.dp))
                                    Text(
                                        text = menuItem.text,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    menuItem.action()
                                },
                            )
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
        }

        if (shortDescription.isNotEmpty() || longDescription.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (shortDescription.isNotEmpty()) {
                    item {
                        Text(
                            text = shortDescription
                        )
                    }
                }
                if (shortDescription.isNotEmpty() && longDescription.isNotEmpty()) {
                    item {
                        HorizontalDivider()
                    }
                }
                if (longDescription.isNotEmpty()) {
                    item {
                        Text(
                            text = longDescription
                        )
                    }
                }
            }
        }
    }
}