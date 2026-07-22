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

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.deprec8.enigmadroid.R

@Composable
fun MediaIntentErrorDialog(onDismissRequest: () -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest, title = {
        Text(text = stringResource(R.string.unable_to_play_media))
    }, text = {
        Text(stringResource(R.string.please_make_sure_that_you_have_at_least_one_media_player_installed_on_your_device))
    }, confirmButton = {
        TextButton(onClick = onDismissRequest) {
            Text(stringResource(R.string.ok))
        }
    })
}

@Composable
fun UrlIntentErrorDialog(onDismissRequest: () -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest, title = {
        Text(text = stringResource(R.string.unable_to_open_url))
    }, text = {
        Text(stringResource(R.string.please_make_sure_that_you_have_at_least_one_browser_installed_on_your_device))
    }, confirmButton = {
        TextButton(onClick = onDismissRequest) {
            Text(stringResource(R.string.ok))
        }
    })
}