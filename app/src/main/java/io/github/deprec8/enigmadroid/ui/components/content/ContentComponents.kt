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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.ContentFlag

@Composable
fun ContentFlagListItem(name: String, flag: ContentFlag) {
    val icon = when (flag) {
        ContentFlag.MARKER -> Icons.Outlined.Bookmark
        ContentFlag.DIRECTORY -> Icons.Outlined.Folder
        ContentFlag.GROUP -> Icons.Outlined.AutoAwesomeMosaic
        else -> null
    }
    val contentDescription = when (flag) {
        ContentFlag.MARKER -> stringResource(R.string.marker)
        ContentFlag.DIRECTORY -> stringResource(R.string.directory)
        ContentFlag.GROUP -> stringResource(R.string.group)
        else -> ""
    }

    if (icon != null) {
        Column {
            ListItem(
                headlineContent = {
                    Text(
                        name, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }, leadingContent = {
                    Icon(icon, contentDescription)
                }, colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun ContentTabRow(selectedTabIndex: Int, content: @Composable () -> Unit) {
    Column(Modifier.padding(top = 8.dp)) {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex, divider = { }, scrollState = rememberScrollState()
        ) {
            content()
        }
        HorizontalDivider()
    }
}

@Composable
fun ContentTab(name: String, selected: Boolean, onClick: () -> Unit) {
    Tab(
        text = {
            Text(
                text = name, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        },
        onClick = {
            onClick()
        },
        selected = selected,
    )
}