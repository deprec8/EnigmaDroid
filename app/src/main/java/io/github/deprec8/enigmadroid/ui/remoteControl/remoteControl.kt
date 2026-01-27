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

package io.github.deprec8.enigmadroid.ui.remoteControl

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ScreenshotMonitor
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.ui.remoteControl.modules.ArrowButtons
import io.github.deprec8.enigmadroid.ui.remoteControl.modules.BouquetButtons
import io.github.deprec8.enigmadroid.ui.remoteControl.modules.ColorButtons
import io.github.deprec8.enigmadroid.ui.remoteControl.modules.ControlButtons
import io.github.deprec8.enigmadroid.ui.remoteControl.modules.MediaButtons
import io.github.deprec8.enigmadroid.ui.remoteControl.modules.NumberButtons
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteControlPage(
    onNavigateBack: () -> Unit, remoteControlViewModel: RemoteControlViewModel = hiltViewModel()
) {

    val loadingState by remoteControlViewModel.loadingState.collectAsStateWithLifecycle()
    val currentDevice by remoteControlViewModel.currentDevice.collectAsStateWithLifecycle()
    val remoteVibration by remoteControlViewModel.remoteVibration.collectAsStateWithLifecycle()

    var showNumbers by rememberSaveable {
        mutableStateOf(false)
    }
    var showMenu by rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val view = LocalView.current

    LaunchedEffect(Unit) {
        remoteControlViewModel.updateLoadingState(false)
    }

    fun performHaptic() {
        if (remoteVibration) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    @Composable
    fun DeviceText() {
        Box {
            AnimatedContent(
                loadingState, label = "", transitionSpec = {
                    scaleIn(
                        initialScale = 0f, animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() togetherWith scaleOut(targetScale = 0f) + fadeOut()
                }) {
                when (it) {
                    LoadingState.LOADED -> Text(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.Center),
                        text = currentDevice?.name ?: "",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    LoadingState.NO_DEVICE_AVAILABLE, LoadingState.DEVICE_NOT_ONLINE, LoadingState.NO_NETWORK_AVAILABLE -> IconButton(
                        onClick = { scope.launch { remoteControlViewModel.updateLoadingState(true) } },
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp)
                            .align(Alignment.Center)
                    ) {
                        Icon(
                            Icons.Default.RestartAlt,
                            contentDescription = stringResource(R.string.retry)
                        )
                    }
                    LoadingState.LOADING -> CircularProgressIndicator(
                        Modifier
                            .padding(12.dp)
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(R.string.remote_control),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, navigationIcon = {
                IconButton(
                    onClick = { onNavigateBack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.navigate_back)
                    )
                }
            }, scrollBehavior = scrollBehavior, actions = {
                Row {
                    DeviceText()
                    if (! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
                        IconButton(
                            onClick = { showNumbers = true },
                            enabled = loadingState == LoadingState.LOADED
                        ) {
                            Icon(
                                Icons.Default.Dialpad,
                                contentDescription = stringResource(R.string.open_number_pad)
                            )
                        }
                    }
                    IconButton(
                        onClick = { showMenu = true }, enabled = loadingState == LoadingState.LOADED
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.open_menu)
                        )
                        DropdownMenu(
                            expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = {
                                Text(
                                    text = stringResource(R.string.screenshot),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }, onClick = {
                                showMenu = false
                                scope.launch {
                                    remoteControlViewModel.fetchScreenshot()
                                }
                            }, leadingIcon = {
                                Icon(
                                    Icons.Default.ScreenshotMonitor, contentDescription = null
                                )
                            })
                            HorizontalDivider()

                            DropdownMenuItem(text = {
                                Text(
                                    text = stringResource(R.string.toggle_standby),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }, onClick = {
                                showMenu = false
                                remoteControlViewModel.power(0)
                            }, leadingIcon = {
                                Icon(
                                    Icons.Default.PowerSettingsNew, contentDescription = null
                                )
                            })
                            DropdownMenuItem(text = {
                                Text(
                                    text = stringResource(R.string.restart),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }, onClick = {
                                showMenu = false
                                remoteControlViewModel.power(2)
                            }, leadingIcon = {
                                Icon(
                                    Icons.Outlined.RestartAlt, contentDescription = null
                                )
                            })
                            DropdownMenuItem(text = {
                                Text(
                                    text = stringResource(R.string.restart_gui),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }, onClick = {
                                showMenu = false
                                remoteControlViewModel.power(3)
                            }, leadingIcon = {
                                Icon(
                                    Icons.Outlined.RestartAlt, contentDescription = null
                                )
                            })
                            DropdownMenuItem(text = {
                                Text(
                                    text = stringResource(R.string.shutdown),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }, onClick = {
                                showMenu = false
                                remoteControlViewModel.power(1)
                            }, leadingIcon = {
                                Icon(
                                    Icons.Outlined.PowerSettingsNew, contentDescription = null
                                )
                            })
                        }
                    }
                }
            })
        }, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
        ) {
            if (! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) || (windowSizeClass.isHeightAtLeastBreakpoint(
                    WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
                ) && ! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND))
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center)
                        .fillMaxSize()
                ) {
                    ColorButtons(
                        {
                            remoteControlViewModel.onButtonClicked(it)
                            performHaptic()
                        }, loadingState == LoadingState.LOADED
                    )
                    ArrowButtons(
                        {
                            remoteControlViewModel.onButtonClicked(it)
                            performHaptic()
                        }, loadingState == LoadingState.LOADED
                    )
                    BouquetButtons(
                        {
                            remoteControlViewModel.onButtonClicked(it)
                            performHaptic()
                        }, loadingState == LoadingState.LOADED
                    )
                    MediaButtons(
                        {
                            remoteControlViewModel.onButtonClicked(it)
                            performHaptic()
                        }, loadingState == LoadingState.LOADED
                    )
                    ControlButtons(
                        {
                            remoteControlViewModel.onButtonClicked(it)
                            performHaptic()
                        }, loadingState == LoadingState.LOADED
                    )
                }
                if (showNumbers) {
                    ModalBottomSheet(
                        onDismissRequest = { showNumbers = false }, sheetState = sheetState
                    ) {
                        Column(
                            Modifier
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NumberButtons(
                                {
                                    remoteControlViewModel.onButtonClicked(it)
                                    performHaptic()
                                }, loadingState == LoadingState.LOADED
                            )
                        }
                    }
                }

            } else {
                Column(
                    Modifier
                        .padding(8.dp)
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .weight(1f)
                        ) {
                            ColorButtons(
                                {
                                    remoteControlViewModel.onButtonClicked(it)
                                    performHaptic()
                                }, loadingState == LoadingState.LOADED
                            )
                            ArrowButtons(
                                {
                                    remoteControlViewModel.onButtonClicked(it)
                                    performHaptic()
                                }, loadingState == LoadingState.LOADED
                            )
                            MediaButtons(
                                {
                                    remoteControlViewModel.onButtonClicked(it)
                                    performHaptic()
                                }, loadingState == LoadingState.LOADED
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .weight(1f)
                        ) {
                            BouquetButtons(
                                {
                                    remoteControlViewModel.onButtonClicked(it)
                                    performHaptic()
                                }, loadingState == LoadingState.LOADED
                            )
                            NumberButtons(
                                {
                                    remoteControlViewModel.onButtonClicked(it)
                                    performHaptic()
                                }, loadingState == LoadingState.LOADED
                            )
                            ControlButtons(
                                {
                                    remoteControlViewModel.onButtonClicked(it)
                                    performHaptic()
                                }, loadingState == LoadingState.LOADED
                            )
                        }
                    }
                }
            }
        }
    }
}