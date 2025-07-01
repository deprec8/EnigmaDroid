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

package io.github.deprec8.enigmadroid.ui.remote

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.horizontalSafeContentPadding
import io.github.deprec8.enigmadroid.ui.remote.modules.ArrowButtons
import io.github.deprec8.enigmadroid.ui.remote.modules.BouquetButtons
import io.github.deprec8.enigmadroid.ui.remote.modules.ColorButtons
import io.github.deprec8.enigmadroid.ui.remote.modules.ControlButtons
import io.github.deprec8.enigmadroid.ui.remote.modules.MediaButtons
import io.github.deprec8.enigmadroid.ui.remote.modules.NumberButtons
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemotePage(
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    remoteViewModel: RemoteViewModel = hiltViewModel()
) {

    var showNumbers by rememberSaveable {
        mutableStateOf(false)
    }
    var showMenu by rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val loadingState by remoteViewModel.loadingState.collectAsStateWithLifecycle()
    val currentDevice by remoteViewModel.currentDevice.collectAsStateWithLifecycle()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val remoteVibration by remoteViewModel.remoteVibration.collectAsStateWithLifecycle()
    val view = LocalView.current

    LaunchedEffect(Unit) {
        remoteViewModel.updateLoadingState(false)
    }

    fun performHaptic(){
        if (remoteVibration){
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    @Composable
    fun DeviceText() {
        Box {
            AnimatedContent(
                loadingState, label = "", transitionSpec =
                    {
                        scaleIn(
                            initialScale = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn() togetherWith
                                scaleOut(targetScale = 0f) + fadeOut()
                    })
            {
                when (it) {
                    0       -> Text(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.Center),
                        text = currentDevice?.name ?: "",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    1, 2    -> IconButton(
                        onClick = { scope.launch { remoteViewModel.updateLoadingState(true) } },
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
                    null, 3 ->
                        CircularProgressIndicator(
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
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                modifier = Modifier.horizontalSafeContentPadding(),
                title = {
                    Text(
                        text = stringResource(R.string.remote),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }, navigationIcon = {
                    IconButton(
                        onClick = { onNavigateBack() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }, scrollBehavior = scrollBehavior, actions = {
                    Row {
                        DeviceText()
                        if (windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.EXPANDED) {
                            IconButton(
                                onClick = { showNumbers = true },
                                enabled = loadingState == 0
                            ) {
                                Icon(
                                    Icons.Default.Dialpad,
                                    contentDescription = stringResource(R.string.open_number_pad)
                                )
                            }
                        }
                        IconButton(onClick = { showMenu = true }, enabled = loadingState == 0) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.open_menu)
                            )
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.screenshot),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        scope.launch {
                                            remoteViewModel.fetchScreenshot()
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.ScreenshotMonitor,
                                            contentDescription = null
                                        )
                                    }
                                )
                                HorizontalDivider()

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.toggle_standby),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        remoteViewModel.power(0)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.PowerSettingsNew,
                                            contentDescription = null
                                        )
                                    })
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.restart),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        remoteViewModel.power(2)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.RestartAlt,
                                            contentDescription = null
                                        )
                                    })
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.restart_gui),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        remoteViewModel.power(3)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.RestartAlt,
                                            contentDescription = null
                                        )
                                    })
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.shutdown),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        remoteViewModel.power(1)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.PowerSettingsNew,
                                            contentDescription = null
                                        )
                                    })
                            }
                        }
                    }
                })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            Modifier
                .consumeWindowInsets(innerPadding)
                .verticalScroll(scrollState)
                .padding(innerPadding)
        ) {
            if (windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.EXPANDED) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                        .fillMaxSize()
                ) {
                    ColorButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                    ArrowButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                    BouquetButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                    MediaButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                    ControlButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                }
                if (showNumbers) {
                    ModalBottomSheet(
                        onDismissRequest = { showNumbers = false },
                        sheetState = sheetState,
                        modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
                    ) {
                        Column(
                            Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NumberButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                        }
                    }
                }

            } else {
                Column(
                    Modifier
                        .padding(16.dp)
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
                                .padding(20.dp)
                                .weight(1f)
                        ) {
                            ColorButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                            ArrowButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                            MediaButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                        }
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .padding(20.dp)
                                .weight(1f)
                        ) {
                            BouquetButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                            NumberButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                            ControlButtons(remoteViewModel, loadingState == 0) { performHaptic() }
                        }
                    }
                }
            }
        }
    }

}