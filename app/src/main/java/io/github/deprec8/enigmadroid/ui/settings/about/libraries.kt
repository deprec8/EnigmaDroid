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

package io.github.deprec8.enigmadroid.ui.settings.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.insets.topAppBarWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.utils.IntentUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrariesPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val libraries by produceLibraries()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        contentWindowInsets = contentWithDrawerWindowInsets(),
        topBar = {
            TopAppBar(windowInsets = topAppBarWithDrawerWindowInsets(), title = {
                Text(
                    text = stringResource(id = R.string.third_party_libraries),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, scrollBehavior = scrollBehavior, navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.navigate_back)
                    )
                }
            })
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding),
            columns = GridCells.Adaptive(350.dp),
            contentPadding = innerPadding
        ) {
            items(libraries?.libraries ?: emptyList()) { library ->
                ListItem(modifier = Modifier.clickable {
                    if (library.website != null) {
                        IntentUtils.openUrl(context, library.website !!)
                    }
                }, trailingContent = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                }, overlineContent = {
                    Text(
                        text = "${library.licenses.first().name} - " + if ((library.artifactVersion?.length
                                ?: 0) > 15
                        ) {
                            "${library.artifactVersion?.substring(0, 10) ?: ""}…"
                        } else {
                            library.artifactVersion ?: ""
                        }
                    )
                }, headlineContent = { Text(text = library.name) }, supportingContent = {
                    Text(
                        text = library.developers.firstOrNull()?.name ?: ""
                    )
                })
            }
        }
    }
}