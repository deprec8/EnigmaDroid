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

package io.github.deprec8.enigmadroid.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R

@Composable
fun LoadingScreen(
    modifier: Modifier,
    updateLoadingState: (forceUpdate: Boolean) -> Unit,
    loadingState: Int?,
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = loadingState, transitionSpec =
                {
                    scaleIn(
                        initialScale = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() togetherWith
                            scaleOut(targetScale = 0f) + fadeOut()
                }, label = ""
        ) {
            when (it) {
                0       -> {
                    Column {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp),
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
                1       -> {
                    Column {
                        Text(
                            text = stringResource(R.string.device_not_connected),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                        OutlinedButton(
                            onClick = { updateLoadingState(true) },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                2       -> {
                    Column {
                        Text(
                            text = stringResource(R.string.no_device_available),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp),
                        )
                        OutlinedButton(
                            onClick = { updateLoadingState(true) },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                null, 3 -> {
                    Column {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp),
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = stringResource(R.string.searching_for_device),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}