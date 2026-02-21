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

package io.github.deprec8.enigmadroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveDialog(
    onDismissRequest: () -> Unit,
    content: @Composable (isContentScrollable: Boolean) -> Unit,
    actionButton: @Composable () -> Unit,
    title: String
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var showCancelDialog by rememberSaveable { mutableStateOf(false) }
    val fullScrollState = rememberScrollState()
    val dialogScrollState = rememberScrollState()
    val isSmallScreenLayout =
        ! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) || ! windowSizeClass.isHeightAtLeastBreakpoint(
            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
        )

    if (showCancelDialog) {
        AlertDialog(onDismissRequest = { showCancelDialog = false }, dismissButton = {
            TextButton(onClick = { showCancelDialog = false }) {
                Text(stringResource(R.string.cancel))
            }
        }, confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                showCancelDialog = false
            }) {
                Text(stringResource(R.string.discard))
            }
        }, title = {
            Text(stringResource(R.string.discard_unsaved_changes))
        }, text = {
            Text(stringResource(R.string.you_may_have_changes_that_won_t_be_saved_if_you_close))
        })
    }

    if (isSmallScreenLayout) {
        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false, decorFitsSystemWindows = false
            ),
            onDismissRequest = {
                showCancelDialog = true
            }
        ) {
            Scaffold(
                containerColor = if (fullScrollState.maxValue == 0) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }, topBar = {
                    TopAppBar(
                        colors = TopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            scrolledContainerColor = MaterialTheme.colorScheme.surface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.primary,
                            subtitleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ), title = {
                            Text(
                                title, maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }, actions = {
                            actionButton()
                        }, navigationIcon = {
                            IconButton(onClick = {
                                showCancelDialog = true
                            }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close)
                                )
                            }
                        })
                }) { innerPadding ->
                Column(
                    Modifier
                        .consumeWindowInsets(innerPadding)
                        .verticalScroll(fullScrollState)
                        .padding(innerPadding)
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
                ) {
                    content(fullScrollState.maxValue != 0)
                }
            }
        }
    } else {
        AlertDialog(onDismissRequest = { onDismissRequest() }, dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(R.string.cancel))
            }
        }, confirmButton = {
            actionButton()
        }, title = { Text(title) }, text = {
            Column(
                Modifier.verticalScroll(dialogScrollState)
            ) {
                content(false)
            }
        })
    }
}