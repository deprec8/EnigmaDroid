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

package io.github.deprec8.enigmadroid.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.DeviceSetupCard
import io.github.deprec8.enigmadroid.ui.components.horizontalSafeContentPadding

@Composable
fun OnboardingPage(
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val passwordVisible by onboardingViewModel.passwordVisible.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            BottomAppBar {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalSafeContentPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { onboardingViewModel.completeOnboarding() },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(stringResource(R.string.skip))
                    }
                    Button(
                        onClick = {
                            if (onboardingViewModel.isEveryFieldFilled()) {
                                onboardingViewModel.addDevice()
                                onboardingViewModel.completeOnboarding()
                            }
                        },
                        enabled = onboardingViewModel.isEveryFieldFilled(),
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(text = stringResource(R.string.finish_setup))
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
                text = stringResource(R.string.start_by_adding_one_device),
                Modifier.padding(horizontal = 16.dp)
            )
            DeviceSetupCard(
                Modifier.padding(16.dp),
                onboardingViewModel.name,
                onboardingViewModel.ip,
                onboardingViewModel.port,
                onboardingViewModel.livePort,
                onboardingViewModel.isHttps,
                onboardingViewModel.isLogin,
                onboardingViewModel.user,
                onboardingViewModel.password,
                passwordVisible,
                { name -> onboardingViewModel.updateName(name) },
                { ip -> onboardingViewModel.updateIp(ip) },
                { port -> onboardingViewModel.updatePort(port) },
                { livePort -> onboardingViewModel.updateLivePort(livePort) },
                { onboardingViewModel.toggleHttps() },
                { onboardingViewModel.toggleLogin() },
                { user -> onboardingViewModel.updateUser(user) },
                { password -> onboardingViewModel.updatePassword(password) },
                { onboardingViewModel.togglePasswordVisibility() }
            )
        }

    }
}