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

package io.github.deprec8.enigmadroid.ui.remoteControl.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.data.source.local.devices.Device

@Composable
fun DeviceText(
    loadingState: LoadingState, currentDevice: Device?, onUpdateLoadingState: () -> Unit
) {
    Box {
        AnimatedContent(
            loadingState, label = "", transitionSpec = {
                scaleIn(
                    initialScale = 0f, animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow
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
                    onClick = { onUpdateLoadingState() },
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