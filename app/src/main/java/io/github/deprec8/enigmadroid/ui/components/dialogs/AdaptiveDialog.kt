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

package io.github.deprec8.enigmadroid.ui.components.dialogs

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.isSmallScreenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveDialog(
    onDismissRequest: () -> Unit,
    content: @Composable (isContentScrollable: Boolean) -> Unit,
    actionButton: @Composable () -> Unit,
    title: String
) {

    var showCancelDialog by rememberSaveable { mutableStateOf(false) }
    val fullScrollState = rememberScrollState()
    val dialogScrollState = rememberScrollState()
    val isSmallScreenLayout = isSmallScreenLayout()
    val isDarkTheme = isSystemInDarkTheme()

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
            Text(stringResource(R.string.discard_warning))
        })
    }

    if (isSmallScreenLayout) {
        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false, decorFitsSystemWindows = false
            ), onDismissRequest = {
                showCancelDialog = true
            }) {
            val view = LocalView.current
            DisposableEffect(view, isDarkTheme) {
                val window = (view.parent as? DialogWindowProvider)?.window

                window?.let { win ->
                    val controller = WindowCompat.getInsetsController(win, view)
                    controller.isAppearanceLightStatusBars = !isDarkTheme
                    controller.isAppearanceLightNavigationBars = !isDarkTheme
                    win.setWindowAnimations(0)
                }
                onDispose {}
            }
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
                            TooltipBox(
                                tooltip = {
                                    PlainTooltip {
                                        Text(stringResource(id = R.string.cancel))
                                    }
                                },
                                state = rememberTooltipState(),
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                    TooltipAnchorPosition.Below, 4.dp
                                )
                            ) {
                                IconButton(onClick = {
                                    showCancelDialog = true
                                }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = stringResource(R.string.cancel)
                                    )
                                }
                            }
                        })
                }) { innerPadding ->
                Column(
                    Modifier
                        .consumeWindowInsets(innerPadding)
                        .verticalScroll(fullScrollState)
                        .padding(innerPadding)
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
                        .imePadding()
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