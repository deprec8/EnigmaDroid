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

package io.github.deprec8.enigmadroid.ui.remotecontrol

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.ui.components.navigation.ArrowNavigationButton
import io.github.deprec8.enigmadroid.ui.remotecontrol.components.ActionMenu
import io.github.deprec8.enigmadroid.ui.remotecontrol.components.ArrowButtons
import io.github.deprec8.enigmadroid.ui.remotecontrol.components.BouquetButtons
import io.github.deprec8.enigmadroid.ui.remotecontrol.components.ColorButtons
import io.github.deprec8.enigmadroid.ui.remotecontrol.components.ControlButtons
import io.github.deprec8.enigmadroid.ui.remotecontrol.components.DeviceText
import io.github.deprec8.enigmadroid.ui.remotecontrol.components.MediaButtons
import io.github.deprec8.enigmadroid.ui.remotecontrol.components.NumberButtons
import io.github.deprec8.enigmadroid.utils.IntentUtils
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteControlPage(
    onNavigateBack: () -> Unit, remoteControlViewModel: RemoteControlViewModel = koinViewModel()
) {

    val connectionState by remoteControlViewModel.connectionState.collectAsStateWithLifecycle()
    val currentDevice by remoteControlViewModel.currentDevice.collectAsStateWithLifecycle()
    val remoteControlVibration by remoteControlViewModel.remoteControlVibration.collectAsStateWithLifecycle()

    var showNumbers by rememberSaveable {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val view = LocalView.current
    val isSmallScreenLayout =
        !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) || (windowSizeClass.isHeightAtLeastBreakpoint(
            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
        ) && !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND))
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val viewString = stringResource(R.string.view)
    val retryString = stringResource(R.string.retry)
    val savedString = stringResource(R.string.screenshot_saved)
    val failedString = stringResource(R.string.screenshot_failed)

    fun performHaptic() {
        if (remoteControlVibration) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    fun fetchScreenshot() {
        scope.launch {
            val uri = remoteControlViewModel.fetchScreenshot()
            if (uri != null) {
                if (remoteControlVibration) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                }

                val result = snackbarHostState.showSnackbar(
                    message = savedString,
                    actionLabel = viewString,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    IntentUtils.openImage(context, uri)
                }
            } else {
                if (remoteControlVibration) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    }
                }

                val result = snackbarHostState.showSnackbar(
                    message = failedString,
                    actionLabel = retryString,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    fetchScreenshot()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }, topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(R.string.remote_control),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, navigationIcon = {
                ArrowNavigationButton { onNavigateBack() }
            }, scrollBehavior = scrollBehavior, actions = {
                Row {
                    DeviceText(connectionState, currentDevice) {
                        remoteControlViewModel.checkConnection()
                    }
                    if (!windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
                        TooltipBox(
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(id = R.string.number_pad))
                                }
                            },
                            state = rememberTooltipState(),
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Below, 4.dp
                            )
                        ) {
                            IconButton(
                                onClick = { showNumbers = true },
                                enabled = connectionState == ConnectionState.CONNECTED
                            ) {
                                Icon(
                                    Icons.Default.Dialpad,
                                    contentDescription = stringResource(R.string.number_pad)
                                )
                            }
                        }
                    }
                    ActionMenu(
                        connectionState == ConnectionState.CONNECTED,
                        { fetchScreenshot() },
                        { remoteControlViewModel.onPowerKeyClicked(it) })
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
            if (isSmallScreenLayout) {
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
                            remoteControlViewModel.onKeyClicked(it)
                            performHaptic()
                        }, connectionState == ConnectionState.CONNECTED
                    )
                    ArrowButtons(
                        {
                            remoteControlViewModel.onKeyClicked(it)
                            performHaptic()
                        }, connectionState == ConnectionState.CONNECTED
                    )
                    BouquetButtons(
                        {
                            remoteControlViewModel.onKeyClicked(it)
                            performHaptic()
                        }, connectionState == ConnectionState.CONNECTED
                    )
                    MediaButtons(
                        {
                            remoteControlViewModel.onKeyClicked(it)
                            performHaptic()
                        }, connectionState == ConnectionState.CONNECTED
                    )
                    ControlButtons(
                        {
                            remoteControlViewModel.onKeyClicked(it)
                            performHaptic()
                        }, connectionState == ConnectionState.CONNECTED
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
                                    remoteControlViewModel.onKeyClicked(it)
                                    performHaptic()
                                }, connectionState == ConnectionState.CONNECTED
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
                                    remoteControlViewModel.onKeyClicked(it)
                                    performHaptic()
                                }, connectionState == ConnectionState.CONNECTED
                            )
                            ArrowButtons(
                                {
                                    remoteControlViewModel.onKeyClicked(it)
                                    performHaptic()
                                }, connectionState == ConnectionState.CONNECTED
                            )
                            MediaButtons(
                                {
                                    remoteControlViewModel.onKeyClicked(it)
                                    performHaptic()
                                }, connectionState == ConnectionState.CONNECTED
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
                                    remoteControlViewModel.onKeyClicked(it)
                                    performHaptic()
                                }, connectionState == ConnectionState.CONNECTED
                            )
                            NumberButtons(
                                {
                                    remoteControlViewModel.onKeyClicked(it)
                                    performHaptic()
                                }, connectionState == ConnectionState.CONNECTED
                            )
                            ControlButtons(
                                {
                                    remoteControlViewModel.onKeyClicked(it)
                                    performHaptic()
                                }, connectionState == ConnectionState.CONNECTED
                            )
                        }
                    }
                }
            }
        }
    }
}