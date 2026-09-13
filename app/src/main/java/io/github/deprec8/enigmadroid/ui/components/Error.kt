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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.core.network.NoCurrentDeviceException
import io.github.deprec8.enigmadroid.core.network.NoDevicesException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.ContentConvertException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.UnknownHostException

@Composable
fun NoResults(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_results), textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InvalidResponse(modifier: Modifier = Modifier, throwable: Throwable, onRetry: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding(), contentAlignment = Alignment.Center
    ) {
        when (throwable) {
            is NoCurrentDeviceException -> InfoDisplay(
                stringResource(R.string.no_device_selected), onRetry
            )

            is NoDevicesException -> InfoDisplay(
                stringResource(R.string.no_devices_found), onRetry
            )

            is UnknownHostException, is ConnectException -> InfoDisplay(
                "DeviceEntity offline or not reachable", onRetry
            )

            is SocketTimeoutException, is HttpRequestTimeoutException, is IOException -> InfoDisplay(
                "Network error / Timeout / IO", onRetry
            )

            is SerializationException, is ContentConvertException -> InfoDisplay(
                "JSON seri error", onRetry
            )

            is ResponseException -> InfoDisplay(
                "HTTP error / server", onRetry
            )

            else -> InfoDisplay(
                "Something went wrong", onRetry
            )
        }
    }
}

@Composable
private fun InfoDisplay(text: String, onReload: () -> Unit) {
    Column {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
        FilledTonalButton(
            onClick = { onReload() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}