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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.DeviceSetupCard

@Composable
fun OnboardingPage(
    onOnboardingFinished: () -> Unit,
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val isEveryFieldFilled by onboardingViewModel.isEveryFieldFilled.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    var showSkipDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { showSkipDialog = true }, modifier = Modifier.padding(16.dp)
                    ) {
                        Text(stringResource(R.string.skip))
                    }
                    Button(
                        onClick = {
                            onboardingViewModel.completeOnboardingWithDevice()
                            onOnboardingFinished()
                        }, enabled = isEveryFieldFilled, modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = stringResource(R.string.finish))
                    }

                }
            }
        }) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .imePadding()
        ) {
            Text(
                text = stringResource(R.string.welcome_to_enigmadroid),
                Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.setup_device_creation_info),
                Modifier.padding(horizontal = 16.dp)
            )
            DeviceSetupCard(
                modifier = Modifier.padding(16.dp),
                nameState = onboardingViewModel.nameState,
                ipState = onboardingViewModel.ipState,
                portState = onboardingViewModel.portState,
                livePortState = onboardingViewModel.livePortState,
                isHttps = onboardingViewModel.isHttps,
                isLogin = onboardingViewModel.isLogin,
                userState = onboardingViewModel.userState,
                passwordState = onboardingViewModel.passwordState,
                onHttpsChange = { onboardingViewModel.toggleHttps() },
                onLoginChange = { onboardingViewModel.toggleLogin() },
            )
        }

    }

    if (showSkipDialog) {
        AlertDialog(onDismissRequest = { showSkipDialog = false }, confirmButton = {
            TextButton(onClick = {
                showSkipDialog = false
                onboardingViewModel.completeOnboarding()
                onOnboardingFinished()
            }) {
                Text(stringResource(R.string.skip))
            }
        }, icon = {
            Icon(Icons.Outlined.SkipNext, null)
        }, title = {
            Text(stringResource(R.string.setup_skip_title))
        }, text = {
            Text(stringResource(R.string.setup_skip_text))
        }, dismissButton = {
            TextButton(onClick = {
                showSkipDialog = false
            }) {
                Text(stringResource(R.string.cancel))
            }
        })
    }
}