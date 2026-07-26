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

package io.github.deprec8.enigmadroid.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.constant.DefaultPorts
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.ui.components.DeviceSetupCard
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private enum class OnboardingStep {
    Welcome, LocalNetworkPermission, DeviceSetup, Finish
}

@Composable
fun OnboardingPage(
    onOnboardingFinished: () -> Unit, onboardingViewModel: OnboardingViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val steps = remember {
        buildList {
            add(OnboardingStep.Welcome)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
                add(OnboardingStep.LocalNetworkPermission)
            }
            add(OnboardingStep.DeviceSetup)
            add(OnboardingStep.Finish)
        }
    }
    val pagerState = rememberPagerState(pageCount = { steps.size })

    Scaffold(
        bottomBar = {
            PagerIndicatorBar(pagerState = pagerState)
        }) { innerPadding ->
        HorizontalPager(
            state = pagerState, userScrollEnabled = false
        ) { pageIndex ->
            when (steps[pageIndex]) {
                OnboardingStep.Welcome -> WelcomeStep(
                    innerPadding, onContinue = {
                        scope.launch {
                            pagerState.animateScrollToPage(pageIndex + 1)
                        }
                    })

                OnboardingStep.LocalNetworkPermission -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
                    LocalNetworkPermissionStep(
                        innerPadding, onContinue = {
                            scope.launch {
                                pagerState.animateScrollToPage(pageIndex + 1)
                            }
                        })
                }

                OnboardingStep.DeviceSetup -> DeviceSetupStep(
                    innerPadding, onContinue = { device ->
                        onboardingViewModel.setDevice(device)
                        scope.launch {
                            pagerState.animateScrollToPage(pageIndex + 1)
                        }
                    })

                OnboardingStep.Finish -> FinishStep(innerPadding, onContinue = {
                    onboardingViewModel.finishOnboarding()
                    onOnboardingFinished()
                })
            }
        }
    }
}

@Composable
private fun PagerIndicatorBar(
    pagerState: PagerState
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(
                BottomAppBarDefaults.windowInsets
            )
            .height(64.dp), contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val current = pagerState.currentPage == iteration
                val completed = pagerState.currentPage > iteration
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (current) {
                                MaterialTheme.colorScheme.primary
                            } else if (completed) {
                                MaterialTheme.colorScheme.outline
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            }
                        )
                        .height(8.dp)
                        .animateContentSize()
                        .width(if (current) 24.dp else 8.dp)
                )
            }
        }
    }
}

@Composable
private fun OnboardingLayout(
    paddingValues: PaddingValues,
    title: String,
    description: String,
    primaryAction: @Composable () -> Unit,
    secondaryAction: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(paddingValues)
            .verticalScroll(scrollState)
            .padding(paddingValues)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content?.invoke()
        }
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(
                    vertical = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            primaryAction()
            secondaryAction?.invoke()
        }
    }
}

@Composable
private fun WelcomeStep(paddingValues: PaddingValues, onContinue: () -> Unit) {
    OnboardingLayout(
        paddingValues = paddingValues,
        title = stringResource(R.string.onboarding_welcome_title),
        description = stringResource(R.string.onboarding_welcome_description),
        primaryAction = {
            Button(
                onClick = onContinue, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.onboarding_welcome_button))
            }
        })
}

@RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
@Composable
private fun LocalNetworkPermissionStep(paddingValues: PaddingValues, onContinue: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onContinue()
    }

    OnboardingLayout(
        paddingValues = paddingValues,
        title = stringResource(R.string.onboarding_permissions_title),
        description = stringResource(R.string.onboarding_permissions_description),
        primaryAction = {
            Button(
                onClick = {
                    launcher.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.onboarding_permissions_button))
            }
        },
        secondaryAction = {
            TextButton(onClick = onContinue) {
                Text(stringResource(R.string.skip))
            }
        })
}

@Composable
private fun DeviceSetupStep(paddingValues: PaddingValues, onContinue: (Device?) -> Unit) {
    var https by rememberSaveable { mutableStateOf(false) }
    var login by rememberSaveable { mutableStateOf(false) }

    val nameState = rememberTextFieldState("")
    val hostState = rememberTextFieldState("")
    val portState = rememberTextFieldState(DefaultPorts.HTTP)
    val livePortState = rememberTextFieldState(DefaultPorts.LIVE)
    val userState = rememberTextFieldState("")
    val passwordState = rememberTextFieldState("")

    val ready by remember {
        derivedStateOf {
            if (nameState.text.isBlank() || hostState.text.isBlank() || portState.text.isBlank() || livePortState.text.isBlank()) return@derivedStateOf false
            return@derivedStateOf if (login) userState.text.isNotBlank() && passwordState.text.isNotBlank() else true
        }
    }

    OnboardingLayout(
        paddingValues = paddingValues,
        title = stringResource(R.string.onboarding_device_setup_title),
        description = stringResource(R.string.onboarding_device_setup_description),
        primaryAction = {
            Button(
                onClick = {
                    if (ready) {
                        onContinue(
                            Device(
                                name = nameState.text.toString().trim(),
                                host = hostState.text.toString(),
                                port = portState.text.toString().toInt(),
                                livePort = livePortState.text.toString().toInt(),
                                https = https,
                                login = login,
                                user = userState.text.toString(),
                                password = passwordState.text.toString(),
                            )
                        )
                    }
                }, enabled = ready, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add_device))
            }
        },
        secondaryAction = {
            TextButton(
                onClick = { onContinue(null) }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.skip))
            }
        }) {
        DeviceSetupCard(
            nameState = nameState,
            hostState = hostState,
            portState = portState,
            livePortState = livePortState,
            https = https,
            login = login,
            userState = userState,
            passwordState = passwordState,
            onHttpsChange = {
                https = !https
                if (portState.text == DefaultPorts.HTTP && https) {
                    portState.setTextAndPlaceCursorAtEnd(DefaultPorts.HTTPS)
                } else if (portState.text == DefaultPorts.HTTPS && !https) {
                    portState.setTextAndPlaceCursorAtEnd(DefaultPorts.HTTP)
                }
            },
            onLoginChange = {
                login = !login
            },
        )
    }
}

@Composable
private fun FinishStep(paddingValues: PaddingValues, onContinue: () -> Unit) {
    OnboardingLayout(
        paddingValues = paddingValues,
        title = stringResource(R.string.onboarding_finish_title),
        description = stringResource(R.string.onboarding_finish_description),
        primaryAction = {
            Button(
                onClick = onContinue, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.onboarding_finish_button))
            }
        })
}